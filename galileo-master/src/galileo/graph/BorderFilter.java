package galileo.graph;

import galileo.comm.PrecisionLimit;

public class BorderFilter {
	
	private PrecisionLimit precision;
	private TemporalBorderFilter tFilter;
	private SpatialBorderFilter sFilter;
	
	public BorderFilter() {}
	
	public BorderFilter(PrecisionLimit prec) {
		
	}
	
	public TemporalBorderFilter gettFilter() {
		return tFilter;
	}
	public void settFilter(TemporalBorderFilter tFilter) {
		this.tFilter = tFilter;
	}
	public SpatialBorderFilter getsFilter() {
		return sFilter;
	}
	public void setsFilter(SpatialBorderFilter sFilter) {
		this.sFilter = sFilter;
	}
	public PrecisionLimit getPrecision() {
		return precision;
	}
	public void setPrecision(PrecisionLimit precision) {
		this.precision = precision;
	}
	
	

}
