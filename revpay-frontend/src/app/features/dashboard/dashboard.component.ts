import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-business-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class BusinessDashboardComponent implements OnInit {

  balance = 0;
  summary: any = {};

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dashboardService.getBalance()
      .subscribe((res: number) => this.balance = res);

    this.dashboardService.getSummary()
      .subscribe((res: any) => this.summary = res);
  }

  // ✅ ADD THESE FUNCTIONS

  sendMoney() {
    this.router.navigate(['/send-money']);
  }

  requestMoney() {
    this.router.navigate(['/request-money']);
  }

  addMoney() {
    this.router.navigate(['/add-money']);
  }

  withdraw() {
    this.router.navigate(['/withdraw']);
  }

}
