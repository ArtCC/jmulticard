package org.bouncycastle.asn1.x509;

import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

/**
 * The CRLNumber object.
 * <pre>
 * CRLNumber::= INTEGER(0..MAX)
 * </pre>
 */
public class CRLNumber
    extends ASN1Object
{
    private final BigInteger number;

    public CRLNumber(
        final BigInteger number)
    {
        if (BigIntegers.ZERO.compareTo(number) > 0)
        {
            throw new IllegalArgumentException("Invalid CRL number : not in (0..MAX)");
        }
        this.number = number;
    }

    public BigInteger getCRLNumber()
    {
        return number;
    }

    @Override
	public String toString()
    {
        return "CRLNumber: " + getCRLNumber();
    }

    @Override
	public ASN1Primitive toASN1Primitive()
    {
        return new ASN1Integer(number);
    }

    public static CRLNumber getInstance(final Object o)
    {
        if (o instanceof CRLNumber)
        {
            return (CRLNumber)o;
        }
        else if (o != null)
        {
            return new CRLNumber(ASN1Integer.getInstance(o).getValue());
        }

        return null;
    }
}
