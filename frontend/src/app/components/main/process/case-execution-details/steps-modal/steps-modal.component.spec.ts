import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepsModalComponent } from './steps-modal.component';

describe('StepsModalComponent', () => {
  let component: StepsModalComponent;
  let fixture: ComponentFixture<StepsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StepsModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
