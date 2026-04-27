import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PortfolioDaily, PortfolioCumulative } from '../models/portfolio.model';

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {
  private apiUrl = environment.portfolioUrl;

  constructor(private http: HttpClient) { }

  getDailyEvolution(symbol: string, startDate: string, endDate: string): Observable<PortfolioDaily[]> {
    const params = new HttpParams()
      .set('simbol', symbol)
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<PortfolioDaily[]>(`${this.apiUrl}/daily`, { params });
  }

  getCumulativeSummary(symbol: string, startDate: string, endDate: string): Observable<PortfolioCumulative> {
    const params = new HttpParams()
      .set('simbol', symbol)
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<PortfolioCumulative>(`${this.apiUrl}/cumulative`, { params });
  }
}
