// Shared Sidebar & Topbar Component for Student Pages

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard
    if (!Auth.isAuthenticated()) {
        window.location.href = '../login.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'STUDENT' && user.role !== 'ADMIN') {
        alert('Access denied. Student authorization required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    // 2. Render Shared Sidebar Layout if container exists
    const appContainer = document.getElementById('app-layout');
    if (appContainer) {
        const currentPath = window.location.pathname.split('/').pop() || 'student-dashboard.html';

        const sidebarHtml = `
        <div class="dashboard-sidebar d-flex flex-column flex-shrink-0 p-3">
            <a href="student-dashboard.html" class="d-flex align-items-center mb-4 text-white text-decoration-none px-2">
                <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width:38px; height:38px;">
                    <i class="bi bi-mortarboard-fill fs-5"></i>
                </div>
                <span class="fs-4 fw-bold">Student Portal</span>
            </a>
            <hr class="text-secondary opacity-25">
            <ul class="nav nav-pills flex-column mb-auto gap-1">
                <li>
                    <a href="student-dashboard.html" class="sidebar-link ${currentPath === 'student-dashboard.html' || currentPath === 'dashboard.html' ? 'active' : ''}">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="counsellors.html" class="sidebar-link ${currentPath === 'counsellors.html' || currentPath === 'book-appointment.html' ? 'active' : ''}">
                        <i class="bi bi-person-badge"></i> Find Counsellors
                    </a>
                </li>
                <li>
                    <a href="my-appointments.html" class="sidebar-link ${currentPath === 'my-appointments.html' || currentPath === 'payment.html' ? 'active' : ''}">
                        <i class="bi bi-calendar-check"></i> My Appointments
                    </a>
                </li>
                <li>
                    <a href="my-tasks.html" class="sidebar-link ${currentPath === 'my-tasks.html' ? 'active' : ''}">
                        <i class="bi bi-card-checklist"></i> My Tasks
                    </a>
                </li>
                <li>
                    <a href="my-results.html" class="sidebar-link ${currentPath === 'my-results.html' ? 'active' : ''}">
                        <i class="bi bi-award"></i> My Results
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
    const nameEl = document.getElementById('headerUserName');
    if (nameEl && user) {
        nameEl.textContent = user.fullName || user.email;
    }
});
