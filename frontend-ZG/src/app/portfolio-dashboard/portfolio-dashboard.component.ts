import { Component, OnInit, HostBinding } from '@angular/core';
import { PortfolioService } from '../services/portfolio.service';
import { PortfolioDaily, PortfolioCumulative, ChartData } from '../models/portfolio.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-portfolio-dashboard',
  templateUrl: './portfolio-dashboard.component.html',
  styleUrls: ['./portfolio-dashboard.component.scss']
})
export class PortfolioDashboardComponent implements OnInit {
  @HostBinding('class.dark-theme') isDarkTheme = false;

  // Filters
  symbol: string = 'BOVA11';
  startDate: string = '2010-05-01';
  endDate: string = '2020-04-30';
  availableSymbols: string[] = ['BOVA11', 'ITUB4F', 'MGLU3F', 'BPAN4F', 'VVAR3F'];


  // Data
  cumulativeData: PortfolioCumulative | null = null;
  dailyEvolution: PortfolioDaily[] = [];
  chartData: any[] = [];
  
  // UI State
  isLoading: boolean = false;
  hasError: boolean = false;
  noData: boolean = false;

  // Chart Options
  view: [number, number] = [700, 400];
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  xAxisLabel = 'Data';
  showYAxisLabel = true;
  yAxisLabel = 'Valor Patrimonial (R$)';
  autoScale = true;
  timeline = true;
  colorScheme: any = {
    domain: ['#10b981'] // Emerald Green
  };

  constructor(private portfolioService: PortfolioService) { }

  ngOnInit(): void {
    this.fetchData();
  }

  toggleTheme(): void {
    this.isDarkTheme = !this.isDarkTheme;
    if (this.isDarkTheme) {
      document.body.classList.add('dark-theme');
    } else {
      document.body.classList.remove('dark-theme');
    }
  }

  fetchData(): void {
    this.isLoading = true;
    this.hasError = false;
    this.noData = false;

    // Fetch Cumulative Summary
    this.portfolioService.getCumulativeSummary(this.symbol, this.startDate, this.endDate)
      .subscribe({
        next: (data) => {
          this.cumulativeData = data;
        },
        error: (err) => {
          console.error('Error fetching cumulative data', err);
          this.hasError = true;
        }
      });

    // Fetch Daily Evolution
    this.portfolioService.getDailyEvolution(this.symbol, this.startDate, this.endDate)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => {
          this.dailyEvolution = data;
          if (data && data.length > 0) {
            this.prepareChartData(data);
          } else {
            this.noData = true;
          }
        },
        error: (err) => {
          console.error('Error fetching daily evolution', err);
          this.hasError = true;
        }
      });
  }

  prepareChartData(data: PortfolioDaily[]): void {
    this.chartData = [
      {
        name: 'Valor Patrimonial',
        series: data.map(item => ({
          name: new Date(item.date),
          value: item.valorAtual,
          extra: { currentPrice: item.currentPrice }
        }))
      }
    ];
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  formatPercentage(value: number | undefined): string {
    if (value === undefined || value === null) return '0,00%';
    return value.toFixed(2).replace('.', ',') + '%';
  }
}
