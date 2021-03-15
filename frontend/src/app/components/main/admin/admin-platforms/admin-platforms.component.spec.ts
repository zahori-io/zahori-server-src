import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AdminPlatformsComponent } from './admin-platforms.component';

describe('AdminPlatformsComponent', () => {
  let component: AdminPlatformsComponent;
  let fixture: ComponentFixture<AdminPlatformsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminPlatformsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPlatformsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
