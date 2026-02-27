import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvoiceService } from '../../../core/services/invoice.service';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss']
})
export class InvoiceListComponent implements OnInit {

  invoices: any[] = [];
  loading = true;

  constructor(private service: InvoiceService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.service.getMyInvoices().subscribe({
      next: (res) => {
        this.invoices = res;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  pay(id: number) {
    this.service.payInvoice(id).subscribe(() => this.load());
  }

  cancel(id: number) {
    this.service.cancelInvoice(id).subscribe(() => this.load());
  }
}
