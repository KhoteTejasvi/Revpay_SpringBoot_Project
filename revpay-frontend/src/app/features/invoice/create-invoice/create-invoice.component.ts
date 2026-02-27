import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvoiceService } from '../../../core/services/invoice.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-invoice',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-invoice.component.html',
  styleUrls: ['./create-invoice.component.scss']
})
export class CreateInvoiceComponent {

  customerEmail = '';
  amount = 0;
  description = '';

  constructor(
    private service: InvoiceService,
    private router: Router
  ) {}

  create() {

    const data = {
      customerEmail: this.customerEmail,
      totalAmount: this.amount,
      description: this.description
    };

    this.service.createInvoice(data).subscribe(() => {
      alert('Invoice Created');
      this.router.navigate(['/invoices']);
    });
  }
}
