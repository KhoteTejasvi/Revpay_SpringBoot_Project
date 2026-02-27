import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  name = '';
  email = '';
  phoneNumber = '';
  password = '';
  confirmPassword = '';
  role = 'USER';

  showPassword = false;
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  register() {

    if (!this.name || !this.email || !this.phoneNumber || !this.password) {
      alert('Please fill all fields');
      return;
    }

    if (this.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    const data = {
      fullName: this.name,
      email: this.email,
      phoneNumber: this.phoneNumber,
      password: this.password,
      role: this.role
    };

    this.loading = true;

    this.auth.register(data).subscribe({

      next: (res: any) => {

        const message = res?.message || 'Registration successful';
        alert(message);

        this.loading = false;

        this.router.navigate(['/login']);
      },

      error: (err) => {

        const message =
          err?.error?.message ||
          err?.error ||
          'Registration failed';

        alert(message);

        this.loading = false;
      }
    });
  }
}
