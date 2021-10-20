import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminResolutionsComponent } from './admin-resolutions.component';

describe('AdminResolutionsComponent', () => {
  let component: AdminResolutionsComponent;
  let fixture: ComponentFixture<AdminResolutionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminResolutionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminResolutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
