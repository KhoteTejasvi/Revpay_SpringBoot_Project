import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BusinessDashboardService } from '../../core/services/business-dashboard.service';

@Component({
  selector: 'app-business-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './business-dashboard.component.html',
  styleUrls: ['./business-dashboard.component.scss']
})
export class BusinessDashboardComponent implements OnInit {

  data: any;
  loading = true;

  constructor(private service: BusinessDashboardService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.service.getDashboard().subscribe({
      next: (res) => {
        console.log('Business Dashboard:', res);
        this.data = res;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }
}
