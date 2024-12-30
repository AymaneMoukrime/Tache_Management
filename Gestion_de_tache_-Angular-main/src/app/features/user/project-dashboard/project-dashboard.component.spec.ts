import { ComponentFixture, TestBed } from '@angular/core/testing';
import { projectDashboardComponent } from './project-dashboard.component';

describe('ProjectDashboardComponent', () => {
  let component: projectDashboardComponent;
  let fixture: ComponentFixture<projectDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [projectDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(projectDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
