document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) {
        window.location.href = '../index.html';
        return;
    }

    const user = Auth.getUser();
    if (user.role !== 'ADMIN') {
        alert('Access denied. Administrator privileges required.');
        Auth.redirectToDashboard(user.role);
        return;
    }

    document.getElementById('adminName').textContent = user.fullName || user.email;

    try {
        const res = await apiCall('/admin/users');
        if (res.success && res.data) {
            console.log('All System Users:', res.data);
            renderUsersTable(res.data);
        }
    } catch (err) {
        console.error('Error fetching admin user list:', err);
    }

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
    });
});

function renderUsersTable(users) {
    const tableBody = document.getElementById('userTableBody');
    if (!tableBody) return;

    if (!users || users.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No users registered yet.</td></tr>`;
        return;
    }

    tableBody.innerHTML = users.map(u => `
        <tr>
            <td>#${u.id}</td>
            <td class="fw-semibold">${u.fullName || 'N/A'}</td>
            <td>${u.email}</td>
            <td>
                <span class="badge ${getRoleBadgeClass(u.role)}">${u.role}</span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${u.id})">
                    <i class="bi bi-trash"></i> Delete
                </button>
            </td>
        </tr>
    `).join('');
}

function getRoleBadgeClass(role) {
    switch (role) {
        case 'ADMIN': return 'bg-danger-subtle text-danger-emphasis';
        case 'COUNSELLOR': return 'bg-warning-subtle text-warning-emphasis';
        case 'STUDENT': return 'bg-primary-subtle text-primary';
        default: return 'bg-secondary-subtle text-secondary';
    }
}

async function deleteUser(id) {
    if (confirm(`Are you sure you want to delete User #${id}?`)) {
        try {
            const res = await apiCall(`/admin/users/${id}`, 'DELETE');
            if (res.success) {
                alert('User deleted successfully!');
                window.location.reload();
            }
        } catch (err) {
            alert('Failed to delete user: ' + err.message);
        }
    }
}
