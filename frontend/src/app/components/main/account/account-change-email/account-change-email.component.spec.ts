import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountChangeEmailComponent } from './account-change-email.component';

describe('AccountChangeEmailComponent', () => {
  let component: AccountChangeEmailComponent;
  let fixture: ComponentFixture<AccountChangeEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountChangeEmailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountChangeEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
