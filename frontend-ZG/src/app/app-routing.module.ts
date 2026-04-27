import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TradeDataComponent } from './trade-data/trade-data.component';
import { PortfolioDashboardComponent } from './portfolio-dashboard/portfolio-dashboard.component';

const routes: Routes = [
  { path: 'dashboard', component: PortfolioDashboardComponent },
  { path: 'legacy', component: TradeDataComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
