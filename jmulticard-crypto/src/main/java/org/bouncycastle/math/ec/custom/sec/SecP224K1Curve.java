package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.encoders.Hex;

public class SecP224K1Curve extends ECCurve.AbstractFp
{
    public static final BigInteger q = SecP224K1FieldElement.Q;

    private static final int SECP224K1_DEFAULT_COORDS = COORD_JACOBIAN;
    private static final ECFieldElement[] SECP224K1_AFFINE_ZS = new ECFieldElement[] { new SecP224K1FieldElement(ECConstants.ONE) }; 

    protected SecP224K1Point infinity;

    public SecP224K1Curve()
    {
        super(q);

        this.infinity = new SecP224K1Point(this, null, null);

        this.a = fromBigInteger(ECConstants.ZERO);
        this.b = fromBigInteger(BigInteger.valueOf(5));
        this.order = new BigInteger(1, Hex.decodeStrict("010000000000000000000000000001DCE8D2EC6184CAF0A971769FB1F7"));
        this.cofactor = BigInteger.valueOf(1);
        this.coord = SECP224K1_DEFAULT_COORDS;
    }

    @Override
	protected ECCurve cloneCurve()
    {
        return new SecP224K1Curve();
    }

    @Override
	public boolean supportsCoordinateSystem(int coord)
    {
        switch (coord)
        {
        case COORD_JACOBIAN:
            return true;
        default:
            return false;
        }
    }

    public BigInteger getQ()
    {
        return q;
    }

    @Override
	public int getFieldSize()
    {
        return q.bitLength();
    }

    @Override
	public ECFieldElement fromBigInteger(BigInteger x)
    {
        return new SecP224K1FieldElement(x);
    }

    @Override
	protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y)
    {
        return new SecP224K1Point(this, x, y);
    }

    @Override
	protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs)
    {
        return new SecP224K1Point(this, x, y, zs);
    }

    @Override
	public ECPoint getInfinity()
    {
        return infinity;
    }

    @Override
	public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len)
    {
        final int FE_INTS = 7;

        final int[] table = new int[len * FE_INTS * 2];
        {
            int pos = 0;
            for (int i = 0; i < len; ++i)
            {
                ECPoint p = points[off + i];
                Nat224.copy(((SecP224K1FieldElement)p.getRawXCoord()).x, 0, table, pos); pos += FE_INTS;
                Nat224.copy(((SecP224K1FieldElement)p.getRawYCoord()).x, 0, table, pos); pos += FE_INTS;
            }
        }

        return new AbstractECLookupTable()
        {
            @Override
			public int getSize()
            {
                return len;
            }

            @Override
			public ECPoint lookup(int index)
            {
                int[] x = Nat224.create(), y = Nat224.create();
                int pos = 0;

                for (int i = 0; i < len; ++i)
                {
                    int MASK = ((i ^ index) - 1) >> 31;

                    for (int j = 0; j < FE_INTS; ++j)
                    {
                        x[j] ^= table[pos + j] & MASK;
                        y[j] ^= table[pos + FE_INTS + j] & MASK;
                    }

                    pos += (FE_INTS * 2);
                }

                return createPoint(x, y);
            }

            @Override
			public ECPoint lookupVar(int index)
            {
                int[] x = Nat224.create(), y = Nat224.create();
                int pos = 0;

                for (int i = 0; i < len; ++i)
                {
                    int MASK = ((i ^ index) - 1) >> 31;

                    for (int j = 0; j < FE_INTS; ++j)
                    {
                        x[j] ^= table[pos + j] & MASK;
                        y[j] ^= table[pos + FE_INTS + j] & MASK;
                    }

                    pos += (FE_INTS * 2);
                }

                return createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y)
            {
                return createRawPoint(new SecP224K1FieldElement(x), new SecP224K1FieldElement(y), SECP224K1_AFFINE_ZS);
            }
        };
    }

    @Override
	public ECFieldElement randomFieldElement(SecureRandom r)
    {
        int[] x = Nat224.create();
        SecP224K1Field.random(r, x);
        return new SecP224K1FieldElement(x);
    }

    @Override
	public ECFieldElement randomFieldElementMult(SecureRandom r)
    {
        int[] x = Nat224.create();
        SecP224K1Field.randomMult(r, x);
        return new SecP224K1FieldElement(x);
    }
}
