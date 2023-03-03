import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TradeDataComponent } from './trade-data.component';

describe('TradeDataComponent', () => {
  let component: TradeDataComponent;
  let fixture: ComponentFixture<TradeDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TradeDataComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TradeDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
