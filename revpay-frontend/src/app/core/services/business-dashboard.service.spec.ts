import { TestBed } from '@angular/core/testing';

import { BusinessDashboardService } from './business-dashboard.service';

describe('BusinessDashboardService', () => {
  let service: BusinessDashboardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessDashboardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
