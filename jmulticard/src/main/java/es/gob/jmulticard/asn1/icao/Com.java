package es.gob.jmulticard.asn1.icao;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.bertlv.BerTlv;

/** EF&#46;COM de aplicación de LDS1 para el eMRTD de ICAO 9393 parte 10.
 * Contiene informaci&oacute;n sobre la versi&oacute;n LDS, informaci&oacute;n sobre
 * la versi&oacute;n de Unicode y una lista de los grupos de datos que están presentes en la aplicaci&oacute;n.
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s. */
public final class Com extends DecoderObject {

	private static final byte TAG_COM = 0x60;
	private static final char DOT = '.';

	private static final Dictionary<Byte, String> DGTAGS = new Hashtable<>(16);
	static {
		DGTAGS.put(Byte.valueOf((byte) 0x61), "DG1"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x75), "DG2"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x63), "DG3"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x76), "DG4"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x65), "DG5"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x66), "DG6"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x67), "DG7"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x68), "DG8"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x69), "DG9"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6a), "DG10"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6b), "DG11"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6c), "DG12"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6d), "DG13"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6e), "DG14"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x6f), "DG15"); //$NON-NLS-1$
		DGTAGS.put(Byte.valueOf((byte) 0x70), "DG16"); //$NON-NLS-1$
	}

	private String ldsVersion = null;
	private String unicodeVersion = null;
	private final List<String> presentDgs = new ArrayList<>();

	@Override
	protected void decodeValue() throws Asn1Exception, TlvException {

		BerTlv tlv = BerTlv.getInstance(getRawDerValue());
		checkTag(tlv.getTag());

		final ByteArrayInputStream tlvs = new ByteArrayInputStream(tlv.getValue());

		tlv = BerTlv.getInstance(tlvs);
		if (tlv.getLength() != 4) {
			throw new Asn1Exception(
				"El valor del TLV de version LDS debe tener exactamente cuarto octetos, pero se han encontrado " + tlv.getLength() //$NON-NLS-1$
			);
		}
		if (tlv.getTag() != 0x01) {
			throw new Asn1Exception(
				"El valor del TLV de version LDS debe tener etiqueta '01', pero se ha encontrado '" + //$NON-NLS-1$
					HexUtils.hexify(new byte[] { tlv.getTag() }, false) + "'" //$NON-NLS-1$
			);
		}
		this.ldsVersion =
			new String(new byte[] { tlv.getValue()[0], tlv.getValue()[1] }) +
				DOT +
					new String(new byte[] { tlv.getValue()[2], tlv.getValue()[3] });

		tlv = BerTlv.getInstance(tlvs);
		if (tlv.getLength() != 6) {
			throw new Asn1Exception(
				"El valor del TLV de version Unicode debe tener exactamente seis octetos, pero se han encontrado " + tlv.getLength() //$NON-NLS-1$
			);
		}
		if (tlv.getTag() != 0x36) {
			throw new Asn1Exception(
				"El valor del TLV de version Unicode debe tener etiqueta '36', pero se ha encontrado '" + //$NON-NLS-1$
					HexUtils.hexify(new byte[] { tlv.getTag() }, false) + "'" //$NON-NLS-1$
			);
		}
		this.unicodeVersion =
			new String(new byte[] { tlv.getValue()[0], tlv.getValue()[1] }) +
				DOT +
					new String(new byte[] { tlv.getValue()[2], tlv.getValue()[3] }) +
						DOT +
							new String(new byte[] { tlv.getValue()[4], tlv.getValue()[5] });

		tlv = BerTlv.getInstance(tlvs);
		if (tlv.getTag() != 0x5c) {
			throw new Asn1Exception(
				"El valor del TLV de lista de rotulos debe tener etiqueta '5C', pero se han encontrado '" + //$NON-NLS-1$
					HexUtils.hexify(new byte[] { tlv.getTag() }, false) + "'" //$NON-NLS-1$
			);
		}
		final byte[] dgList = tlv.getValue();
		for (final byte dgTag : dgList) {
			this.presentDgs.add(DGTAGS.get(Byte.valueOf(dgTag)));
		}

	}

	@Override
	public String toString() {
		return "Common Data (COM): Version de LDS = " +  this.ldsVersion + //$NON-NLS-1$
			", version de Unicode = " + this.unicodeVersion + //$NON-NLS-1$
				", Grupos de datos presentes = " + this.presentDgs; //$NON-NLS-1$
	}

	@Override
	protected byte getDefaultTag() {
		return TAG_COM;
	}

	/** Obtiene el n&uacute;mero de versi&oacute;n LDS.
	 * @return N&uacute;mero de versi&oacute;n LDS con formato <i>aa.bb</i>, donde <i>aa</i>
	 *         define la versi&oacute;n de la LDS y <i>bb</i> define el nivel de actualizaci&oacute;n. */
	public String getLdsVersion() {
		return this.ldsVersion;
	}

	/** Obtiene la versi&oacute;n de Unicode usada.
	 * @return N&uacute;mero de versi&oacute;n Unicode con formato <i>aa.bb.cc</i>, donde <i>aa</i>
	 *         define la versi&oacute;n principal, <i>bb</i> define la versi&oacute;n menor y
	 *         <i>cc</i> define el nivel de difusi&oacute;n. */
	public String getUnicodeVersion() {
		return this.unicodeVersion;
	}

	/** Obtiene la lista de r&oacute;tulos.
	 * @return Lista de todos los grupos de datos presentes. */
	public String[] getPresentDgs() {
		return (String[]) this.presentDgs.toArray();
	}

}
