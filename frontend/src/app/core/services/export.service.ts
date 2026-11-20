import { Injectable } from '@angular/core';

/** Export de listes (tableau d'en-tetes + lignes) en CSV, Excel (.xlsx) et PDF. */
@Injectable({ providedIn: 'root' })
export class ExportService {

  /** CSV avec separateur ';' et BOM UTF-8 : s'ouvre directement dans Excel (accents corrects). */
  csv(filename: string, headers: string[], rows: (string | number)[][]): void {
    const esc = (v: string | number) => {
      const s = String(v ?? '');
      return /[";\n]/.test(s) ? '"' + s.replace(/"/g, '""') + '"' : s;
    };
    const content = [headers, ...rows].map(r => r.map(esc).join(';')).join('\r\n');
    const blob = new Blob(['﻿' + content], { type: 'text/csv;charset=utf-8;' });
    this.download(blob, filename + '.csv');
  }

  async excel(filename: string, sheetName: string, headers: string[], rows: (string | number)[][]): Promise<void> {
    const XLSX = await import('xlsx');
    const ws = XLSX.utils.aoa_to_sheet([headers, ...rows]);
    ws['!cols'] = headers.map((h, i) => ({
      wch: Math.min(40, Math.max(h.length, ...rows.map(r => String(r[i] ?? '').length)) + 2)
    }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, sheetName);
    XLSX.writeFile(wb, filename + '.xlsx');
  }

  async pdf(filename: string, title: string, headers: string[], rows: (string | number)[][]): Promise<void> {
    const { jsPDF } = await import('jspdf');
    const autoTable = (await import('jspdf-autotable')).default;
    const doc = new jsPDF({ orientation: 'landscape' });
    doc.setFontSize(16);
    doc.text(title, 14, 16);
    doc.setFontSize(9);
    doc.text('Genere le ' + new Date().toLocaleDateString('fr-FR'), 14, 22);
    autoTable(doc, {
      head: [headers],
      body: rows.map(r => r.map(c => String(c ?? ''))),
      startY: 27,
      styles: { fontSize: 9 },
      headStyles: { fillColor: [64, 81, 137] }
    });
    doc.save(filename + '.pdf');
  }

  private download(blob: Blob, name: string): void {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = name;
    a.click();
    URL.revokeObjectURL(url);
  }
}
