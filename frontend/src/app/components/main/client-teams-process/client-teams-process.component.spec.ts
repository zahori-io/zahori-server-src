import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientTeamsProcessComponent } from './client-teams-process.component';

describe('ClientTeamsProcessComponent', () => {
  let component: ClientTeamsProcessComponent;
  let fixture: ComponentFixture<ClientTeamsProcessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClientTeamsProcessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientTeamsProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
