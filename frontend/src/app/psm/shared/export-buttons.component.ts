import { Component, EventEmitter, Output } from '@angular/core';

/** Menu deroulant "Exporter" : emet le format choisi (csv | excel | pdf). */
@Component({
  selector: 'app-export-buttons',
  template: `
    <div ngbDropdown class="d-inline-block">
      <button type="button" class="btn btn-soft-secondary" ngbDropdownToggle>
        <i class="ri-download-2-line align-bottom me-1"></i> Exporter
      </button>
      <div ngbDropdownMenu>
        <button ngbDropdownItem type="button" (click)="export.emit('csv')">
          <i class="ri-file-text-line me-2 text-muted"></i>CSV</button>
        <button ngbDropdownItem type="button" (click)="export.emit('excel')">
          <i class="ri-file-excel-2-line me-2 text-success"></i>Excel (.xlsx)</button>
        <button ngbDropdownItem type="button" (click)="export.emit('pdf')">
          <i class="ri-file-pdf-line me-2 text-danger"></i>PDF</button>
      </div>
    </div>`
})
export class ExportButtonsComponent {
  @Output() export = new EventEmitter<'csv' | 'excel' | 'pdf'>();
}
