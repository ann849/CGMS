// Shared Sidebar & Topbar Component for Counsellor Pages

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard
    if (!Auth.isAuthenticated()) {
        window.location.href = '../login.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'COUNSELLOR' && user.role !== 'ADMIN') {
        alert('Access denied. Counsellor authorization required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    // 2. Render Shared Sidebar Layout if container exists
    const appContainer = document.getElementById('app-layout');
    if (appContainer) {
        const currentPath = window.location.pathname.split('/').pop() || 'counsellor-dashboard.html';

        const sidebarHtml = `
        <div class="dashboard-sidebar d-flex flex-column flex-shrink-0 p-3">
            <a href="counsellor-dashboard.html" class="d-flex align-items-center mb-4 text-white text-decoration-none px-2">
                <div class="bg-warning text-dark rounded-circle d-flex align-items-center justify-content-center me-2" style="width:38px; height:38px;">
                    <i class="bi bi-person-badge-fill fs-5"></i>
                </div>
                <span class="fs-4 fw-bold">Counsellor</span>
            </a>
            <hr class="text-secondary opacity-25">
            <ul class="nav nav-pills flex-column mb-auto gap-1">
                <li>
                    <a href="counsellor-dashboard.html" class="sidebar-link ${currentPath === 'counsellor-dashboard.html' || currentPath === 'dashboard.html' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="my-profile.html" class="sidebar-link ${currentPath === 'my-profile.html' ? 'active' : ''}">
                        <i class="bi bi-person-circle"></i> My Profile
                    </a>
                </li>
                <li>
                    <a href="availability.html" class="sidebar-link ${currentPath === 'availability.html' ? 'active' : ''}">
                        <i class="bi bi-clock-history"></i> Manage Slots
                    </a>
                </li>
                <li>
                    <a href="appointments.html" class="sidebar-link ${currentPath === 'appointments.html' ? 'active' : ''}">
                        <i class="bi bi-calendar-check"></i> Appointments
                    </a>
                </li>
                <li>
                    <a href="assign-task.html" class="sidebar-link ${currentPath === 'assign-task.html' ? 'active' : ''}">
                        <i class="bi bi-plus-square"></i> Assign Task
                    </a>
                </li>
                <li>
                    <a href="submissions.html" class="sidebar-link ${currentPath === 'submissions.html' ? 'active' : ''}">
                        <i class="bi bi-file-earmark-check"></i> Submissions
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
    const nameEl = document.getElementById('headerCounsellorName');
    if (nameEl && user) {
        nameEl.textContent = user.fullName || user.email;
    }
});
