import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResolutionComponentComponent } from './resolution-component.component';

describe('ResolutionComponentComponent', () => {
  let component: ResolutionComponentComponent;
  let fixture: ComponentFixture<ResolutionComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResolutionComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResolutionComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
