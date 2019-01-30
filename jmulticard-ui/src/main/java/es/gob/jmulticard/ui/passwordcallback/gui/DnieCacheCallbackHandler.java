package es.gob.jmulticard.ui.passwordcallback.gui;

import java.util.Timer;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import es.gob.jmulticard.callback.CustomAuthorizeCallback;
import es.gob.jmulticard.callback.CustomTextInputCallback;
import es.gob.jmulticard.card.dnie.CacheElement;
import es.gob.jmulticard.ui.passwordcallback.DialogBuilder;
import es.gob.jmulticard.ui.passwordcallback.Messages;

/** CallbackHandler que gestiona los Callbacks de petici&oacute;n de informaci&oacute;n al usuario
 * cuando utiliza un DNIe. Esta clase cachea las respuestas de confirmaci&oacute;n y contrase&mtilde;a
 * del usuario de tal forma que no requerir&aacute;a que las vuelva a introducir. La cach&eacute; se
 * borra autom&aacute;ticamente pasado un tiempo determinado. */
public final class DnieCacheCallbackHandler implements CallbackHandler, CacheElement {

	private static final Logger LOGGER = Logger.getLogger("es.gob.jmulticard"); //$NON-NLS-1$

	private static final long CACHE_TIMEOUT = 3600 * 1000;	// 1 hora

	private char[] currentPassword = null;

	private boolean confirmed = false;

	private Timer timer = null;

	@Override
	public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {

		if (callbacks != null) {
			for (final Callback cb : callbacks) {
				if (cb != null) {
					if (cb instanceof CustomTextInputCallback) {
						final UIPasswordCallbackCan uip = new UIPasswordCallbackCan(
							Messages.getString("CanPasswordCallback.0"), //$NON-NLS-1$
							null,
							Messages.getString("CanPasswordCallback.0"), //$NON-NLS-1$
							Messages.getString("CanPasswordCallback.2") //$NON-NLS-1$
						);
						((CustomTextInputCallback) cb).setText(new String(uip.getPassword()));
						return;
					}
					else if (cb instanceof CustomAuthorizeCallback) {
						if (!this.confirmed) {
							DialogBuilder.showSignatureConfirmDialog((CustomAuthorizeCallback) cb);
							this.confirmed = ((CustomAuthorizeCallback) cb).isAuthorized();
						}
						((CustomAuthorizeCallback) cb).setAuthorized(this.confirmed);
						return;
					}
					else if (cb instanceof PasswordCallback) {

						synchronized (LOGGER) {
							if (this.currentPassword == null) {
								final CommonPasswordCallback uip = new CommonPasswordCallback(
										((PasswordCallback)cb).getPrompt(),
										Messages.getString("CommonPasswordCallback.1"), //$NON-NLS-1$
										true
										);
								this.currentPassword = uip.getPassword();

								LOGGER.info("Guardamos en cache la contrasena de la tarjeta"); //$NON-NLS-1$
							}
							((PasswordCallback)cb).setPassword(this.currentPassword);
						}

						 // Si no se ha hecho ya, programamos una tarea para el borrado de la contrasena cacheada para
						// que se ejecute en un tiempo determinado
						if (this.timer == null) {
							this.timer = new Timer();
							this.timer.schedule(new ResetCacheTimerTask(this), CACHE_TIMEOUT);
						}
						return;
					}
					LOGGER.severe("Callback no soportada: " + cb.getClass().getName()); //$NON-NLS-1$
				}
			}
		}
		else {
			LOGGER.warning("Se ha recibido un array de Callbacks nulo"); //$NON-NLS-1$
		}
		throw new UnsupportedCallbackException(null);
	}

	@Override
	public void reset() {

		LOGGER.info("Eliminamos de cache la contrasena de la tarjeta"); //$NON-NLS-1$

		synchronized (LOGGER) {
			this.currentPassword = null;
			this.confirmed = false;
		}

		if (this.timer != null) {
			this.timer.cancel();
			this.timer.purge();
			this.timer = null;
		}
	}
}