import {Link} from './link';

export class Resolution {
  resolutionId: number;
  active: boolean;
  order: number;
  width: number;
  height: number;
  widthAndHeight: string;
  name: string;
  constructor() {
    this.resolutionId = 0;
    this.active = true;
    this.order = 0;
    this.width = 0;
    this.height = 0;
    this.widthAndHeight = '';
    this.name = '';
  }

}
