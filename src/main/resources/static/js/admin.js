document.addEventListener('DOMContentLoaded', () => {
    requireAuth('ADMIN');
    loadDashboardData();
});

window.switchTab = (tabId) => {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.sidebar-item').forEach(el => el.classList.remove('active'));
    
    document.getElementById(`tab-${tabId}`).classList.add('active');
    event.target.classList.add('active');

    if (tabId === 'users') loadUsers();
    else if (tabId === 'exams') loadExams();
    else if (tabId === 'results') loadResults();
    else if (tabId === 'dashboard') loadDashboardData();
};

window.logout = () => {
    localStorage.clear();
    window.location.href = 'login.html';
};

async function loadDashboardData() {
    try {
        const [users, exams, results] = await Promise.all([
            api.get('/admin/users'),
            api.get('/admin/exams'),
            api.get('/admin/results')
        ]);
        document.getElementById('stat-users').textContent = users.length;
        document.getElementById('stat-exams').textContent = exams.length;
        document.getElementById('stat-results').textContent = results.length;
    } catch (error) {
        console.error('Failed to load stats', error);
    }
}

async function loadUsers() {
    try {
        const users = await api.get('/admin/users');
        const tbody = document.getElementById('users-table-body');
        tbody.innerHTML = users.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td><span style="padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; background: ${u.role === 'ADMIN' ? 'var(--primary-500)' : 'var(--bg-input)'}">${u.role}</span></td>
                <td>
                    <button class="btn btn-outline" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="deleteUser(${u.id})">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Failed to load users', error);
    }
}

window.deleteUser = async (id) => {
    if (confirm('Are you sure you want to delete this user?')) {
        try {
            await api.delete(`/admin/users/${id}`);
            loadUsers();
        } catch (error) {
            alert('Failed to delete user');
        }
    }
};

async function loadExams() {
    try {
        const exams = await api.get('/admin/exams');
        const tbody = document.getElementById('exams-table-body');
        tbody.innerHTML = exams.map(e => `
            <tr>
                <td>${e.id}</td>
                <td>${e.title}</td>
                <td>${e.duration}</td>
                <td>${e.totalQuestions}</td>
                <td>
                    <button class="btn btn-outline" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="deleteExam(${e.id})">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Failed to load exams', error);
    }
}

window.deleteExam = async (id) => {
    if (confirm('Are you sure you want to delete this exam?')) {
        try {
            await api.delete(`/admin/exam/${id}`);
            loadExams();
        } catch (error) {
            alert('Failed to delete exam');
        }
    }
};

async function loadResults() {
    try {
        const results = await api.get('/admin/results');
        const tbody = document.getElementById('results-table-body');
        
        if (results.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted)">No results found.</td></tr>`;
            return;
        }

        tbody.innerHTML = results.map(r => `
            <tr>
                <td>${r.id}</td>
                <td>${r.user ? r.user.name : '-'}</td>
                <td>${r.exam ? r.exam.title : '-'}</td>
                <td>${r.score}</td>
                <td>
                    <span style="padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; background: ${r.status === 'PASS' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)'}; color: ${r.status === 'PASS' ? 'var(--success)' : 'var(--danger)'}">
                        ${r.status}
                    </span>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Failed to load results', error);
    }
}

// Modal Logic
window.openExamModal = () => document.getElementById('exam-modal').classList.add('active');
window.closeExamModal = () => document.getElementById('exam-modal').classList.remove('active');

document.getElementById('create-exam-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const title = document.getElementById('exam-title').value;
    const description = document.getElementById('exam-desc').value;
    const duration = parseInt(document.getElementById('exam-duration').value);
    const passingMarks = parseInt(document.getElementById('exam-passing').value);
    const totalQuestions = parseInt(document.getElementById('exam-totalq').value);

    try {
        await api.post('/admin/exam', { title, description, duration, passingMarks, totalQuestions, isActive: true });
        closeExamModal();
        loadExams();
    } catch (error) {
        alert('Failed to create exam');
    }
});
