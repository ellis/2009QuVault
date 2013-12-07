package net.ellisw.quvault.parser;

public class MathlibValue {
	private double[][] m_re;
	private double[][] m_im;
	private int m_rows;
	private int m_columns;
	private int m_rowOffset;
	private int m_colOffset;
	private boolean m_bRef;

	
	public MathlibValue(int rows, int columns) {
		m_re = new double[rows][columns];
		m_im = new double[rows][columns];
		m_rows = rows;
		m_columns = columns;
	}
	
	public MathlibValue(MathlibValue ref, int row, int column, int rows, int columns) {
		m_re = ref.m_re;
		m_im = ref.m_im;
		m_rowOffset = row;
		m_colOffset = column;
		m_rows = rows;
		m_columns = columns;
		m_bRef = true;
	}
	
	public MathlibValue(MathlibValue ref) {
		this(ref, 0, 0, ref.m_rows, ref.m_columns);
	}
	
	public static MathlibValue parse(String s) {
		MathlibValue v = new MathlibValue(1, 1);
		String sNum = s;
		char c = s.charAt(s.length() - 1);
		boolean bIm = false;
		if (c == 'i' || c == 'I' || c == 'j' || c == 'J') {
			sNum = s.substring(0, s.length() - 1);
			if (sNum.isEmpty())
				sNum = "1";
			bIm = true;
		}
		double n = Double.parseDouble(sNum);
		if (bIm)
			v.m_im[0][0] = n;
		else
			v.m_re[0][0] = n;
		return v;
	}
	
	public static MathlibValue createNull() {
		MathlibValue v = new MathlibValue(0, 0);
		return v;
	}
	
	public static MathlibValue createScalar(double re, double im) {
		MathlibValue v = new MathlibValue(1, 1);
		v.setAt(0, 0, re, im);
		return v;
	}
	
	public void setAt(int row, int column, double re, double im) {
		m_re[row + m_rowOffset][column + m_colOffset] = re;
		m_im[row + m_rowOffset][column + m_colOffset] = im;
	}
	
	public int getRows() { return m_re.length; }
	public int getCols() { return m_re[0].length; }

	public double[][] re() { return m_re; }
	public double[][] im() { return m_im; }
	
	public MathlibValue negative() {
		MathlibValue v = new MathlibValue(m_rows, m_columns);
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				v.m_re[iRow][iCol] = -this.m_re[iRow + m_rowOffset][iCol + m_colOffset];
				v.m_im[iRow][iCol] = -this.m_im[iRow + m_rowOffset][iCol + m_colOffset];
			}
		}
		return v;
		
	}

	public MathlibValue add(MathlibValue that) {
		if (!hasSameDimensions(that))
			return createNull();
		
		MathlibValue v = new MathlibValue(m_rows, m_columns);
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				v.m_re[iRow][iCol] = this.m_re[iRow + m_rowOffset][iCol + m_colOffset] + that.m_re[iRow + that.m_rowOffset][iCol + that.m_colOffset];
				v.m_im[iRow][iCol] = this.m_im[iRow + m_rowOffset][iCol + m_colOffset] + that.m_im[iRow + that.m_rowOffset][iCol + that.m_colOffset];
			}
		}
		return v;
	}

	public MathlibValue sub(MathlibValue that) {
		if (!hasSameDimensions(that))
			return createNull();
		
		MathlibValue v = new MathlibValue(m_rows, m_columns);
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				v.m_re[iRow][iCol] = this.m_re[iRow + m_rowOffset][iCol + m_colOffset] - that.m_re[iRow + that.m_rowOffset][iCol + that.m_colOffset];
				v.m_im[iRow][iCol] = this.m_im[iRow + m_rowOffset][iCol + m_colOffset] - that.m_im[iRow + that.m_rowOffset][iCol + that.m_colOffset];
			}
		}
		return v;
	}

	public MathlibValue mult(MathlibValue that) {
		MathlibValue v = new MathlibValue(m_rows, m_columns);
		double[] reim = new double[2];
		for (int iRowA = 0; iRowA < m_rows; iRowA++) {
			for (int iColB = 0; iColB < that.getCols(); iColB++) {
				double re = 0;
				double im = 0;
				for (int i = 0; i < m_columns; i++) {
					complexMult(
							this.m_re[iRowA][i], this.m_im[iRowA][i],
							that.m_re[i][iColB], that.m_im[i][iColB],
							reim
							);
					re += reim[0];
					im += reim[1];
				}
				v.m_re[iRowA][iColB] = re;
				v.m_im[iRowA][iColB] = im;
			}
		}
		return v;
	}
	
	public MathlibValue div(MathlibValue that) {
		if (this.isNull() || that.isNull())
			return createNull();
		
		// FIXME: implement this for matricies
		MathlibValue v = new MathlibValue(1, 1);
		double[] reim = new double[2];
		complexDiv(
				this.m_re[0][0], this.m_im[0][0],
				that.m_re[0][0], that.m_im[0][0],
				reim);
		v.m_re[0][0] = reim[0]; 
		v.m_im[0][0] = reim[1]; 
		return v;
	}
	
	public MathlibValue not() {
		MathlibValue v = new MathlibValue(m_rows, m_columns);
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				boolean bZero = (
						this.m_re[iRow + m_rowOffset][iCol + m_colOffset] == 0 &&
						this.m_im[iRow + m_rowOffset][iCol + m_colOffset] == 0); 
				v.m_re[iRow][iCol] = (bZero) ? 1 : 0;
				v.m_im[iRow][iCol] = 0;
			}
		}
		return v;
	}
	
	public MathlibValue pow(MathlibValue that) {
		if (isNull())
			return createNull();
		
		// FIXME: implement this for complex numbers and matricies
		MathlibValue v = new MathlibValue(1, 1);
		v.m_re[0][0] = Math.pow(this.m_re[0][0], that.m_re[0][0]);
		return v;
	}
	
	public MathlibValue getValueForAssignment() {
		MathlibValue v = this;
		if (m_bRef) {
			v = new MathlibValue(m_rows, m_columns);
			for (int iRow = 0; iRow < m_rows; iRow++) {
				for (int iCol = 0; iCol < m_columns; iCol++) {
					v.m_re[iRow][iCol] = this.m_re[iRow + m_rowOffset][iCol + m_colOffset];
					v.m_im[iRow][iCol] = this.m_im[iRow + m_rowOffset][iCol + m_colOffset];
				}
			}
		}
		return v;
	}
	
	private static void complexMult(double a, double b, double c, double d, double[] reim) {
		reim[0] = a*c - b*d;
		reim[1] = a*d + b*c;
	}
	
	private static void complexDiv(double a, double b, double c, double d, double[] reim) {
		double nDenom = c*c + d*d;
		reim[0] = (a*c + b*d) / nDenom;
		reim[1] = (-a*d + b*c) / nDenom;
	}
	
	public boolean hasSameDimensions(MathlibValue that) {
		return (this.m_rows == that.m_rows && this.m_columns == that.m_columns);
	}
	
	public boolean isNull() {
		return (m_rows == 0 || m_columns == 0);
	}
	
	@Override
	public String toString() {
		boolean bComplex = false;
		boolean bDouble = false;
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				double re = m_re[iRow][iCol];
				double im = m_im[iRow][iCol];
				if (im != 0)
					bComplex = true;
				if (re != Math.round(re) || im != Math.round(im))
					bDouble = true;
			}
		}
		
		String[] sRes = new String[m_rows * m_columns];
		String[] sIms = new String[m_rows * m_columns];
		int i = 0;
		int nMaxStringLen = 0;
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				String sNum = toString(m_re[iRow][iCol], bDouble);
				sRes[i] = sNum;
				if (sNum.length() > nMaxStringLen)
					nMaxStringLen = sNum.length();

				if (bComplex) {
					double im = m_im[iRow][iCol];
					sNum = toString(Math.abs(im), bDouble);
					sIms[i] = sNum;
					if (sNum.length() > nMaxStringLen)
						nMaxStringLen = sNum.length();
				}
				i++;
			}
		}
		
		
		String s = "";
		int nFirstIndent = 1;
		if (m_rows > 1 || m_columns > 1) {
			s = "\n\n";
			nFirstIndent = 2;
		}
		
		i = 0;
		for (int iRow = 0; iRow < m_rows; iRow++) {
			if (iRow > 0)
				s += "\n";
			s += String.format("%" + nFirstIndent + "s", " ");
			for (int iCol = 0; iCol < m_columns; iCol++) {
				if (iCol > 0)
					s += "  ";
				
				s += String.format("%" + nMaxStringLen + "s", sRes[i]);
				
				if (bComplex) {
					double im = m_im[iRow][iCol];
					if (im >= 0)
						s += " +";
					else
						s += " -";
					s += String.format("%" + nMaxStringLen + "si", sIms[i]);
				}
				
				i++;
			}
		}

		return s;
	}
	
	private String toString(double n, boolean bDouble) {
		if (bDouble)
			return String.format("% .4f", n);
		else
			return String.format("% .0f", n);
	}
	
	public boolean equals(MathlibValue that) {
		if (this.m_rows != that.m_rows || this.m_columns != that.m_columns)
			return false;
		
		for (int iRow = 0; iRow < m_rows; iRow++) {
			for (int iCol = 0; iCol < m_columns; iCol++) {
				if (this.m_re[iRow + m_rowOffset][iCol + m_colOffset] != that.m_re[iRow + that.m_rowOffset][iCol + that.m_colOffset])
					return false;
				if (this.m_im[iRow + m_rowOffset][iCol + m_colOffset] != that.m_im[iRow + that.m_rowOffset][iCol + that.m_colOffset])
					return false;
			}
		}
		
		return true;
	}
}
