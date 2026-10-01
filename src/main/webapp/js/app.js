/**
 * TẦNG VIEW — JavaScript
 * Giao tiếp với REST API (Tầng Controller) qua Fetch API.
 * Không dùng thư viện ngoài — vanilla JS thuần.
 */

'use strict';

// ── Cấu hình ──────────────────────────────────────────────────────
const APP_NAME = 'ThanhtoanNganhang';
const API_BASE = `/${APP_NAME}/api/thanhtoan`;

// Mã giao dịch QR đang chờ xác nhận
let pendingQrMaGiaoDich = null;

// ══════════════════════════════════════════════════════════════════
//  KHỞI ĐỘNG SAU KHI DOM SẴN SÀNG
// ══════════════════════════════════════════════════════════════════
document.addEventListener('DOMContentLoaded', () => {
  initTabs();
  initNav();
  initForms();
  initQuickAmounts();
  initQrActions();
  initLichSu();
  initModal();
});

// ══════════════════════════════════════════════════════════════════
//  ĐIỀU HƯỚNG (NAV)
// ══════════════════════════════════════════════════════════════════
function initNav() {
  document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', e => {
      e.preventDefault();
      const target = link.dataset.tab;

      document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
      link.classList.add('active');

      document.querySelectorAll('.page').forEach(p => {
        p.classList.remove('active');
        p.classList.add('hidden');
      });

      const page = document.getElementById(`page-${target}`);
      if (page) {
        page.classList.remove('hidden');
        page.classList.add('active');
        if (target === 'lichsu') loadLichSu();
      }
    });
  });
}

// ══════════════════════════════════════════════════════════════════
//  TABS (Chuyển khoản / QR)
// ══════════════════════════════════════════════════════════════════
function initTabs() {
  document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
      const panelId = tab.getAttribute('aria-controls');

      document.querySelectorAll('.tab').forEach(t => {
        t.classList.remove('active');
        t.setAttribute('aria-selected', 'false');
      });
      document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      tab.setAttribute('aria-selected', 'true');
      document.getElementById(panelId)?.classList.add('active');
    });
  });
}

// ══════════════════════════════════════════════════════════════════
//  QUICK AMOUNT BUTTONS
// ══════════════════════════════════════════════════════════════════
function initQuickAmounts() {
  document.querySelectorAll('.amount-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.dataset.target;
      const val      = btn.dataset.val;
      const input    = document.getElementById(targetId);
      if (input) {
        input.value = val;
        input.dispatchEvent(new Event('input'));
      }
    });
  });
}

// ══════════════════════════════════════════════════════════════════
//  FORM — CHUYỂN KHOẢN
// ══════════════════════════════════════════════════════════════════
function initForms() {

  // ── Form chuyển khoản ─────────────────────────────────────────
  document.getElementById('form-ck').addEventListener('submit', async e => {
    e.preventDefault();
    if (!validateFormCK()) return;

    const payload = {
      loaiGiaoDich:    'CHUYEN_KHOAN',
      soTaiKhoanNguon: v('ck-nguon'),
      soTaiKhoanDich:  v('ck-dich'),
      nganHangDich:    v('ck-nganhang') || null,
      soTien:          parseFloat(v('ck-tien')),
      noiDung:         v('ck-noidung') || null
    };

    showOverlay(true);
    try {
      const res = await apiPost(`${API_BASE}/chuyenkhoan`, payload);
      if (res.success) {
        showModalSuccess(
          '✅ Chuyển khoản thành công!',
          `Mã GD: <strong>${res.maGiaoDich}</strong><br>
           Số tiền: <strong>${formatMoney(res.soTien)} VNĐ</strong><br>
           Thời gian: <strong>${formatDate(res.thoiGian)}</strong>`
        );
        document.getElementById('form-ck').reset();
      } else {
        showModalFail('❌ Giao dịch thất bại', res.message);
      }
    } catch (err) {
      showModalFail('❌ Lỗi kết nối', err.message);
    } finally {
      showOverlay(false);
    }
  });

  // ── Form QR ───────────────────────────────────────────────────
  document.getElementById('form-qr').addEventListener('submit', async e => {
    e.preventDefault();
    if (!validateFormQR()) return;

    const payload = {
      loaiGiaoDich:    'QR',
      soTaiKhoanNguon: v('qr-nguon'),
      soTaiKhoanDich:  v('qr-dich'),
      nganHangDich:    v('qr-nganhang'),
      soTien:          parseFloat(v('qr-tien')),
      noiDung:         v('qr-noidung') || null
    };

    showOverlay(true);
    try {
      const res = await apiPost(`${API_BASE}/qr`, payload);
      if (res.success) {
        pendingQrMaGiaoDich = res.maGiaoDich;
        renderQrResult(res, payload);
        showToast('Mã QR đã được tạo — hãy quét để thanh toán', 'info');
      } else {
        showModalFail('❌ Không tạo được QR', res.message);
      }
    } catch (err) {
      showModalFail('❌ Lỗi kết nối', err.message);
    } finally {
      showOverlay(false);
    }
  });
}

// ══════════════════════════════════════════════════════════════════
//  QR RESULT ACTIONS
// ══════════════════════════════════════════════════════════════════
function initQrActions() {

  // Tải ảnh QR
  document.getElementById('btn-download-qr').addEventListener('click', () => {
    const img = document.getElementById('qr-img');
    if (!img.src) return;
    const a = document.createElement('a');
    a.href     = img.src;
    a.download = `QR_${pendingQrMaGiaoDich || 'payment'}.png`;
    a.target   = '_blank';
    a.click();
  });

  // Xác nhận đã quét QR
  document.getElementById('btn-xacnhan-qr').addEventListener('click', async () => {
    if (!pendingQrMaGiaoDich) return;
    showOverlay(true);
    try {
      const res = await apiPut(`${API_BASE}/qr/xacnhan/${pendingQrMaGiaoDich}`);
      if (res.success) {
        showModalSuccess(
          '✅ Thanh toán QR thành công!',
          `Mã GD: <strong>${res.maGiaoDich}</strong><br>
           Số tiền: <strong>${formatMoney(res.soTien)} VNĐ</strong><br>
           Thời gian: <strong>${formatDate(res.thoiGian)}</strong>`
        );
        hideQrResult();
        document.getElementById('form-qr').reset();
        pendingQrMaGiaoDich = null;
      } else {
        showToast(res.message, 'error');
      }
    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      showOverlay(false);
    }
  });
}

function renderQrResult(res, payload) {
  document.getElementById('qr-img').src = res.qrImageUrl;
  document.getElementById('qr-info').textContent =
    `Số tiền: ${formatMoney(payload.soTien)} VNĐ · TK: ${payload.soTaiKhoanDich}` +
    (payload.noiDung ? ` · ${payload.noiDung}` : '');
  document.getElementById('qr-result').classList.remove('hidden');
  document.getElementById('qr-result').scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function hideQrResult() {
  document.getElementById('qr-result').classList.add('hidden');
  document.getElementById('qr-img').src = '';
}

// ══════════════════════════════════════════════════════════════════
//  LỊCH SỬ GIAO DỊCH
// ══════════════════════════════════════════════════════════════════
function initLichSu() {
  document.getElementById('btn-filter').addEventListener('click', loadLichSu);
  document.getElementById('btn-reload').addEventListener('click', () => {
    document.getElementById('filter-stk').value = '';
    loadLichSu();
  });
  document.getElementById('filter-stk').addEventListener('keydown', e => {
    if (e.key === 'Enter') loadLichSu();
  });
}

async function loadLichSu() {
  const stk    = v('filter-stk');
  const url    = stk ? `${API_BASE}/lichsu?stk=${encodeURIComponent(stk)}`
                     : `${API_BASE}/lichsu`;
  const tbody  = document.getElementById('lichsu-body');
  tbody.innerHTML = '<tr><td colspan="10" class="text-center">Đang tải...</td></tr>';

  try {
    const data = await apiGet(url);
    if (!Array.isArray(data) || data.length === 0) {
      tbody.innerHTML = '<tr><td colspan="10" class="text-center">Không có giao dịch nào</td></tr>';
      return;
    }
    tbody.innerHTML = data.map((g, i) => `
      <tr>
        <td>${i + 1}</td>
        <td style="font-family:monospace;font-size:.78rem">${shortId(g.maGiaoDich)}</td>
        <td>${badgeLoai(g.loaiGiaoDich)}</td>
        <td>${esc(g.soTaiKhoanNguon)}</td>
        <td>${esc(g.soTaiKhoanDich)}</td>
        <td>${esc(g.nganHangDich || '—')}</td>
        <td style="font-weight:700;color:var(--primary)">${formatMoney(g.soTien)} ₫</td>
        <td>${esc(g.noiDung || '—')}</td>
        <td>${formatDate(g.thoiGian)}</td>
        <td>${badgeTrangThai(g.trangThai)}</td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="10" class="text-center" style="color:var(--danger)">
      Lỗi tải dữ liệu: ${esc(err.message)}</td></tr>`;
  }
}

// ══════════════════════════════════════════════════════════════════
//  VALIDATION
// ══════════════════════════════════════════════════════════════════
function validateFormCK() {
  let ok = true;
  ok = required('ck-nguon', 'err-ck-nguon', 'Vui lòng nhập số tài khoản nguồn') && ok;
  ok = required('ck-dich',  'err-ck-dich',  'Vui lòng nhập số tài khoản nhận')  && ok;
  ok = validateMoney('ck-tien', 'err-ck-tien') && ok;
  return ok;
}

function validateFormQR() {
  let ok = true;
  ok = required('qr-nguon',    'err-qr-nguon',    'Vui lòng nhập số tài khoản của bạn') && ok;
  ok = required('qr-dich',     'err-qr-dich',     'Vui lòng nhập số tài khoản nhận')    && ok;
  ok = required('qr-nganhang', 'err-qr-nganhang', 'Vui lòng chọn ngân hàng nhận')       && ok;
  ok = validateMoney('qr-tien', 'err-qr-tien') && ok;
  return ok;
}

function required(inputId, errId, msg) {
  const el = document.getElementById(inputId);
  const errEl = document.getElementById(errId);
  if (!el.value.trim()) {
    el.classList.add('error');
    errEl.textContent = msg;
    el.addEventListener('input', () => { el.classList.remove('error'); errEl.textContent = ''; }, { once: true });
    return false;
  }
  return true;
}

function validateMoney(inputId, errId) {
  const el  = document.getElementById(inputId);
  const errEl = document.getElementById(errId);
  const val = parseFloat(el.value);
  if (!el.value || isNaN(val) || val < 1000) {
    el.classList.add('error');
    errEl.textContent = 'Số tiền tối thiểu 1.000 VNĐ';
    el.addEventListener('input', () => { el.classList.remove('error'); errEl.textContent = ''; }, { once: true });
    return false;
  }
  return true;
}

// ══════════════════════════════════════════════════════════════════
//  MODAL
// ══════════════════════════════════════════════════════════════════
function initModal() {
  document.getElementById('modal-close').addEventListener('click', hideModal);
  document.getElementById('modal-result').addEventListener('click', e => {
    if (e.target === e.currentTarget) hideModal();
  });
}

function showModalSuccess(title, detail) {
  document.getElementById('modal-icon').textContent  = '🎉';
  document.getElementById('modal-title').textContent = title;
  document.getElementById('modal-msg').textContent   = '';
  document.getElementById('modal-detail').innerHTML  = detail;
  document.getElementById('modal-result').classList.remove('hidden');
}

function showModalFail(title, msg) {
  document.getElementById('modal-icon').textContent  = '😞';
  document.getElementById('modal-title').textContent = title;
  document.getElementById('modal-msg').textContent   = msg || '';
  document.getElementById('modal-detail').innerHTML  = '';
  document.getElementById('modal-result').classList.remove('hidden');
}

function hideModal() {
  document.getElementById('modal-result').classList.add('hidden');
}

// ══════════════════════════════════════════════════════════════════
//  TOAST
// ══════════════════════════════════════════════════════════════════
function showToast(msg, type = 'info', duration = 3500) {
  const toast = document.getElementById('toast');
  toast.textContent = msg;
  toast.className   = `toast ${type} show`;
  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.classList.add('hidden'), 300);
  }, duration);
  toast.classList.remove('hidden');
}

// ══════════════════════════════════════════════════════════════════
//  OVERLAY
// ══════════════════════════════════════════════════════════════════
function showOverlay(show) {
  document.getElementById('overlay').classList.toggle('hidden', !show);
}

// ══════════════════════════════════════════════════════════════════
//  API HELPERS
// ══════════════════════════════════════════════════════════════════
async function apiGet(url) {
  const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
  return res.json();
}

async function apiPost(url, body) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    body: JSON.stringify(body)
  });
  return res.json();
}

async function apiPut(url) {
  const res = await fetch(url, {
    method: 'PUT',
    headers: { 'Accept': 'application/json' }
  });
  return res.json();
}

// ══════════════════════════════════════════════════════════════════
//  UTILITIES
// ══════════════════════════════════════════════════════════════════
function v(id) {
  return (document.getElementById(id)?.value ?? '').trim();
}

function esc(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function shortId(uuid) {
  return uuid ? uuid.substring(0, 8).toUpperCase() + '...' : '';
}

function formatMoney(amount) {
  if (amount == null) return '0';
  return Number(amount).toLocaleString('vi-VN');
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  try {
    // Jakarta JSON-B serialize LocalDateTime dạng mảng [y,m,d,h,min,s,ns]
    if (Array.isArray(dateStr)) {
      const [y, mo, d, h, mi, s] = dateStr;
      return `${pad(d)}/${pad(mo)}/${y} ${pad(h)}:${pad(mi)}:${pad(s)}`;
    }
    const dt = new Date(dateStr);
    if (isNaN(dt)) return String(dateStr);
    return dt.toLocaleString('vi-VN');
  } catch { return String(dateStr); }
}

function pad(n) { return String(n).padStart(2, '0'); }

function badgeTrangThai(tt) {
  const map = {
    SUCCESS: ['badge-success', 'Thành công'],
    PENDING: ['badge-pending', 'Chờ xác nhận'],
    FAILED:  ['badge-failed',  'Thất bại'],
  };
  const [cls, label] = map[tt] ?? ['', tt];
  return `<span class="badge ${cls}">${label}</span>`;
}

function badgeLoai(loai) {
  if (loai === 'QR')           return '<span class="badge badge-qr">QR</span>';
  if (loai === 'CHUYEN_KHOAN') return '<span class="badge badge-ck">Chuyển khoản</span>';
  return `<span class="badge">${esc(loai)}</span>`;
}
