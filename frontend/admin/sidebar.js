// Shared Sidebar & Topbar Component for Admin Pages

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard
    if (!Auth.isAuthenticated()) {
        window.location.href = '../login.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'ADMIN') {
        alert('Access denied. Administrator privileges required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    // 2. Render Shared Sidebar Layout if container exists
    const appContainer = document.getElementById('app-layout');
    if (appContainer) {
        const currentPath = window.location.pathname.split('/').pop() || 'admin-dashboard.html';

        const sidebarHtml = `
        <div class="dashboard-sidebar d-flex flex-column flex-shrink-0 p-3">
            <a href="admin-dashboard.html" class="d-flex align-items-center mb-4 text-white text-decoration-none px-2">
                <div class="bg-danger text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width:38px; height:38px;">
                    <i class="bi bi-shield-lock-fill fs-5"></i>
                </div>
                <span class="fs-4 fw-bold">Admin Portal</span>
            </a>
            <hr class="text-secondary opacity-25">
            <ul class="nav nav-pills flex-column mb-auto gap-1">
                <li>
                    <a href="admin-dashboard.html" class="sidebar-link ${currentPath === 'admin-dashboard.html' || currentPath === 'dashboard.html' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="manage-students.html" class="sidebar-link ${currentPath === 'manage-students.html' ? 'active' : ''}">
                        <i class="bi bi-mortarboard"></i> Manage Students
                    </a>
                </li>
                <li>
                    <a href="manage-counsellors.html" class="sidebar-link ${currentPath === 'manage-counsellors.html' ? 'active' : ''}">
                        <i class="bi bi-person-badge"></i> Manage Counsellors
                    </a>
                </li>
                <li>
                    <a href="payments.html" class="sidebar-link ${currentPath === 'payments.html' ? 'active' : ''}">
                        <i class="bi bi-receipt"></i> Payment Audits
                    </a>
                </li>
                <li>
                    <a href="reports.html" class="sidebar-link ${currentPath === 'reports.html' ? 'active' : ''}">
                        <i class="bi bi-bar-chart-line"></i> Analytics & Reports
                    </a>
                </li>
            </ul>
            <hr class="text-secondary opacity-25">
            <div class="px-2 mb-2">
                <div class="small text-muted mb-1">Logged in as:</div>
                <div class="fw-semibold text-white text-truncate">${user.fullName || user.email}</div>
            </div>
            <a href="#" id="logoutBtn" class="sidebar-link text-danger"><i class="bi bi-box-arrow-right"></i> Sign Out</a>
        </div>
        `;

        appContainer.insertAdjacentHTML('afterbegin', sidebarHtml);

        document.getElementById('logoutBtn').addEventListener('click', (e) => {
            e.preventDefault();
            Auth.logout();
        });
    }

    // Set Header Name if element exists
    const nameEl = document.getElementById('headerAdminName');
    if (nameEl && user) {
        nameEl.textContent = user.fullName || user.email;
    }
});
