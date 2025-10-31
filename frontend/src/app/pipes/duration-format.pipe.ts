import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'durationFormat'
})
export class DurationFormatPipe implements PipeTransform {
  transform(value: number | null | undefined): any {
    if (value == null || value < 0) return { value: '', unit: '', display: '' };
    if (value < 60) {
      return {
        value: value,
        unit: 'seconds',
        display: value + ' s'
      };
    } else {
      const min = (value / 60).toFixed(1).replace(',', '.');
      return {
        value: min,
        unit: 'minutes',
        display: min + ' m'
      };
    }
  }
}
