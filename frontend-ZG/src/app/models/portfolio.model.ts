export interface PortfolioDaily {
  date: string;
  simbol: string;
  qtdAcoes: number;
  custoTotal: number;
  valorAtual: number;
  returnPercentage: number;
  currentPrice: number;
}

export interface PortfolioCumulative {
  date: string;
  simbol: string;
  qtdAcoes: number;
  custoTotal: number;
  valorAtual: number;
  returnPercentage: number;
  currentPrice: number;
}

export interface ChartDataSeries {
  name: string;
  value: number;
}

export interface ChartData {
  name: string;
  series: ChartDataSeries[];
}
