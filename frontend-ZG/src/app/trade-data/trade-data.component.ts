import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-trade-data',
  templateUrl: './trade-data.component.html',
  styleUrls: ['./trade-data.component.scss']
})
export class TradeDataComponent {

  apiUrl = environment.apiUrl;

  companies: string[] = ['ITUB4F', 'MGLU3F', 'BPAN4F', 'BOVA11', 'VVAR3F', 'TODOS'];

  startDate: string = '2010-05-01';
  endDate: string = '2020-04-30';
  selectedCompany: string = this.companies[5];
  noDataAvailable: boolean = false;

  tradeData: any[] = [];

  constructor(private http: HttpClient) { }

  getTradeData() {

    var url = `${this.apiUrl}?startDate=${this.startDate}&endDate=${this.endDate}`;

    if (this.selectedCompany != 'TODOS') {
      url += `&simbol=${this.selectedCompany}`
    }

    this.http.get<any[]>(url).subscribe(data => {

      if (data && data.length > 0) {

        var dados = []

        for (let i = 0; i < data.length; i++) {

          let actionName = Object.keys(data[i])[0];
          let porcentagem = eval(`data[${i}].${actionName}.porcentagem`)
          let reais = parseFloat(eval(`data[${i}].${actionName}.reais`))

          let valorComPorcentagem

          let retornoErro = ''

          if (isNaN(porcentagem)) {
            retornoErro = porcentagem
          } else {
            const temp = parseFloat(porcentagem)
            valorComPorcentagem = (reais * (1 + (temp / 100))).toFixed(2);;
          }

          dados.push({
            symbol: actionName,
            percentage: isNaN(porcentagem) == true ? retornoErro : porcentagem,
            valueInReais: reais,
            valorComPorcentagem: valorComPorcentagem != null ? valorComPorcentagem : '--'
          });

        }

        this.tradeData = dados;


      } else {
        this.tradeData = [];
        this.noDataAvailable = true;
      }

    }, error => {
      console.log(error);
      this.tradeData = [];
      this.noDataAvailable = true;
    }
    );
  }
}
