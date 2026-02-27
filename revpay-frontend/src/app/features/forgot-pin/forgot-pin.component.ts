import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-pin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot-pin.component.html',
  styleUrls: ['./forgot-pin.component.scss']
})
export class ForgotPinComponent {

  email = '';
  newPin = '';
  confirmPin = '';

  constructor(private http: HttpClient, private router: Router) {}

  resetPin() {

    if (this.newPin !== this.confirmPin) {
      alert('PIN does not match ❌');
      return;
    }

    const payload = {
      email: this.email,
      newPin: this.newPin
    };

    this.http.post(
      'http://localhost:8080/api/auth/forgot-transaction-pin',
      payload
    ).subscribe({
      next: () => {
        alert('Transaction PIN Reset Successfully ✅');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.log(err);
        alert(err.error || 'Failed to reset PIN ❌');
      }
    });
  }

  goBack() {
    this.router.navigate(['/login']);
  }

}
