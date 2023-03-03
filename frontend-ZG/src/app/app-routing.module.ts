import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TradeDataComponent } from './trade-data/trade-data.component';

const routes: Routes = [
  { path: '', component: TradeDataComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
