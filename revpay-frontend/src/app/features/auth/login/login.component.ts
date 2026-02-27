import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  showPassword: boolean = false;
  loading: boolean = false;

  constructor(private auth: AuthService, private router: Router) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  login() {

    if (!this.email || !this.password) {
      Swal.fire('Error', 'Please enter email and password', 'error');
      return;
    }

    const data = {
      email: this.email,
      password: this.password
    };

    this.loading = true;

    this.auth.login(data).subscribe({
      next: (res: any) => {

        this.loading = false;

        this.auth.saveToken(res.token);

        const role = this.auth.getUserRole();

        Swal.fire({
          icon: 'success',
          title: 'Login Successful',
          timer: 1500,
          showConfirmButton: false
        }).then(() => {

          if (role === 'PERSONAL') {
            this.router.navigate(['/dashboard']);
          } else {
            this.router.navigate(['/business-dashboard']);
          }

        });

      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        Swal.fire('Error', 'Invalid email or password', 'error');
      }
    });
  }

}
