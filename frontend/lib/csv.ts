/**
 * Minimal CSV writer for client-side exports.
 * Quotes per RFC 4180 and defuses spreadsheet formula injection, since
 * party names and notes are free text.
 */

function escapeField(value: string): string {
  let field = value;
  // a leading = + - @ would execute as a formula in Excel/Sheets
  if (/^[=+\-@]/.test(field)) {
    field = `'${field}`;
  }
  if (/[",\r\n]/.test(field)) {
    field = `"${field.replace(/"/g, '""')}"`;
  }
  return field;
}

export function toCsv(rows: string[][]): string {
  return rows.map((row) => row.map(escapeField).join(",")).join("\r\n");
}

export function downloadCsv(filename: string, csv: string) {
  // BOM so Excel detects UTF-8 (₹ and other non-ASCII survive)
  const blob = new Blob(["\uFEFF" + csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  URL.revokeObjectURL(url);
}
