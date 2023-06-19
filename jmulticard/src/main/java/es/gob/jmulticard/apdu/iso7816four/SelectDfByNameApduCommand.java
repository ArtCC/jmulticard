/*
 * Controlador Java de la Secretaria de Estado de Administraciones Publicas
 * para el DNI electronico.
 *
 * El Controlador Java para el DNI electronico es un proveedor de seguridad de JCA/JCE
 * que permite el acceso y uso del DNI electronico en aplicaciones Java de terceros
 * para la realizacion de procesos de autenticacion, firma electronica y validacion
 * de firma. Para ello, se implementan las funcionalidades KeyStore y Signature para
 * el acceso a los certificados y claves del DNI electronico, asi como la realizacion
 * de operaciones criptograficas de firma con el DNI electronico. El Controlador ha
 * sido disenado para su funcionamiento independiente del sistema operativo final.
 *
 * Copyright (C) 2012 Direccion General de Modernizacion Administrativa, Procedimientos
 * e Impulso de la Administracion Electronica
 *
 * Este programa es software libre y utiliza un licenciamiento dual (LGPL 2.1+
 * o EUPL 1.1+), lo cual significa que los usuarios podran elegir bajo cual de las
 * licencias desean utilizar el codigo fuente. Su eleccion debera reflejarse
 * en las aplicaciones que integren o distribuyan el Controlador, ya que determinara
 * su compatibilidad con otros componentes.
 *
 * El Controlador puede ser redistribuido y/o modificado bajo los terminos de la
 * Lesser GNU General Public License publicada por la Free Software Foundation,
 * tanto en la version 2.1 de la Licencia, o en una version posterior.
 *
 * El Controlador puede ser redistribuido y/o modificado bajo los terminos de la
 * European Union Public License publicada por la Comision Europea,
 * tanto en la version 1.1 de la Licencia, o en una version posterior.
 *
 * Deberia recibir una copia de la GNU Lesser General Public License, si aplica, junto
 * con este programa. Si no, consultelo en <http://www.gnu.org/licenses/>.
 *
 * Deberia recibir una copia de la European Union Public License, si aplica, junto
 * con este programa. Si no, consultelo en <http://joinup.ec.europa.eu/software/page/eupl>.
 *
 * Este programa es distribuido con la esperanza de que sea util, pero
 * SIN NINGUNA GARANTIA; incluso sin la garantia implicita de comercializacion
 * o idoneidad para un proposito particular.
 */
package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.apdu.CommandApdu;

/** APDU ISO 7816-4 de selecci&oacute;n de DF por nombre.
 * @author Carlos Gamuci Mill&aacute;n
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s. */
public final class SelectDfByNameApduCommand extends CommandApdu {

    /** Instrucci&oacute;n <i>Select File</i> de ISO 7816-4. */
    private static final byte INS_SELECT_FILE = (byte) 0xA4;

    private static final byte SELECT_DF_BY_NAME = (byte) 0x04;

    /** Crea una APDU ISO 7816-4 para la selecci&oacute;n del fichero por nombre.
     * @param cla Clase (CLA) de la APDU.
     * @param name Nombre del fichero. */
    public SelectDfByNameApduCommand(final byte cla, final byte[] name) {
        super(
    		cla,											// CLA
    		SelectDfByNameApduCommand.INS_SELECT_FILE,		// INS
    		SelectDfByNameApduCommand.SELECT_DF_BY_NAME,	// P1
    		(byte) 0x00,									// P2
    		name,									      	// Data
    		null											// Le
		);
    }
}
