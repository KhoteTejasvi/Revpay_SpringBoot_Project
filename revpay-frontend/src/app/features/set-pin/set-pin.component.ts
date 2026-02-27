import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-set-pin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './set-pin.component.html',
  styleUrls: ['./set-pin.component.scss']
})
export class SetPinComponent {

  pin = '';
  confirmPin = '';

  constructor(private http: HttpClient, private router: Router) {}

  setPin() {

    if (this.pin !== this.confirmPin) {
      alert('PIN does not match');
      return;
    }

    this.http.post('http://localhost:8080/api/auth/set-transaction-pin', {
      transactionPin: this.pin
    }).subscribe({
      next: () => {
        alert('PIN Set Successfully ✅');
        this.router.navigate(['/dashboard']);
      },
      error: () => alert('Failed to set PIN')
    });
  }

}
