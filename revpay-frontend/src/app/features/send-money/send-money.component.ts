import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { WalletService } from '../../core/services/wallet.service';

@Component({
  selector: 'app-send-money',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './send-money.component.html',
  styleUrls: ['./send-money.component.scss']
})
export class SendMoneyComponent {

  receiverEmail = '';
  amount = 0;
  note = '';

  constructor(
    private router: Router,
    private walletService: WalletService
  ) {}

  transactionPin = '';

  sendMoney() {

    const payload = {
      receiverEmail: this.receiverEmail,
      amount: this.amount,
      note: this.note,
      transactionPin: this.transactionPin
    };

    this.walletService.transferMoney(payload)
      .subscribe({
        next: () => {
          alert('Money Sent Successfully ✅');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          alert(err.error || 'Transaction Failed ❌');
        }
      });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
