import { TestBed } from '@angular/core/testing';

import { VfiwebService } from './vfiweb.service';

describe('VfiwebService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VfiwebService = TestBed.get(VfiwebService);
    expect(service).toBeTruthy();
  });
});
