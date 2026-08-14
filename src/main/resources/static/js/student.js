document.addEventListener('DOMContentLoaded', async () => {
    requireAuth('STUDENT');
    const user = getUser();
    document.getElementById('user-email').textContent = user.email.split('@')[0];

    try {
        const [exams, results] = await Promise.all([
            api.get('/admin/exams'),
            api.get(`/student/results/${user.email}`)
        ]);

        renderStats(exams, results);
        renderCharts(results);
        renderAvailableExams(exams);
        
        document.getElementById('loading').style.display = 'none';
        document.getElementById('dashboard-content').style.display = 'block';
    } catch (error) {
        console.error('Failed to load dashboard:', error);
    }

    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'login.html';
    });
});

function renderStats(exams, results) {
    const totalExams = exams.length;
    const attemptedExams = results.length;
    const avgScore = attemptedExams > 0 
        ? (results.reduce((acc, curr) => acc + curr.score, 0) / attemptedExams).toFixed(1) 
        : 0;
    const passRate = attemptedExams > 0 
        ? ((results.filter(r => r.status === 'PASS').length / attemptedExams) * 100).toFixed(0) 
        : 0;

    document.getElementById('stat-total').textContent = totalExams;
    document.getElementById('stat-attempted').textContent = attemptedExams;
    document.getElementById('stat-avg').textContent = avgScore;
    document.getElementById('stat-pass').textContent = `${passRate}%`;
}

function renderAvailableExams(exams) {
    const container = document.getElementById('exams-container');
    if (exams.length === 0) {
        container.innerHTML = '<p class="text-muted">No exams available currently.</p>';
        return;
    }

    container.innerHTML = exams.map(exam => `
        <div class="card" style="background: rgba(30, 41, 59, 0.5);">
            <h4 style="margin-bottom: 0.5rem; font-size: 1.1rem;">${exam.title}</h4>
            <p class="text-muted" style="font-size: 0.9rem; margin-bottom: 1.5rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">${exam.description}</p>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-size: 0.8rem; padding: 0.2rem 0.5rem; background: rgba(59, 130, 246, 0.1); color: var(--primary-500); border-radius: 4px;">Duration: ${exam.duration}m</span>
                <button class="btn btn-primary" style="padding: 0.4rem 1rem; font-size: 0.9rem;" onclick="takeExam(${exam.id})">Take Exam</button>
            </div>
        </div>
    `).join('');
}

window.takeExam = (id) => {
    window.location.href = `exam.html?id=${id}`;
};

function renderCharts(results) {
    // Canvas Line Chart (Score Trend)
    const lineCanvas = document.getElementById('line-chart');
    if (lineCanvas && results.length > 0) {
        const ctx = lineCanvas.getContext('2d');
        const width = lineCanvas.width = lineCanvas.parentElement.clientWidth;
        const height = lineCanvas.height = 250;
        
        ctx.clearRect(0, 0, width, height);
        
        // Take last 5 results
        const data = results.slice(-5).map(r => r.score);
        const maxScore = Math.max(...data, 10); // Minimum scale of 10
        
        const padding = 30;
        const graphWidth = width - padding * 2;
        const graphHeight = height - padding * 2;
        
        // Draw grid
        ctx.strokeStyle = '#334155';
        ctx.lineWidth = 1;
        ctx.beginPath();
        for(let i=0; i<=4; i++) {
            const y = padding + (graphHeight / 4) * i;
            ctx.moveTo(padding, y);
            ctx.lineTo(width - padding, y);
        }
        ctx.stroke();

        // Draw line
        if (data.length > 0) {
            ctx.strokeStyle = '#3b82f6';
            ctx.lineWidth = 3;
            ctx.beginPath();
            
            const stepX = graphWidth / Math.max(data.length - 1, 1);
            
            data.forEach((score, index) => {
                const x = padding + (index * stepX);
                const y = padding + graphHeight - ((score / maxScore) * graphHeight);
                if (index === 0) ctx.moveTo(x, y);
                else ctx.lineTo(x, y);
            });
            ctx.stroke();
            
            // Draw points
            ctx.fillStyle = '#3b82f6';
            data.forEach((score, index) => {
                const x = padding + (index * stepX);
                const y = padding + graphHeight - ((score / maxScore) * graphHeight);
                ctx.beginPath();
                ctx.arc(x, y, 5, 0, Math.PI * 2);
                ctx.fill();
            });
        }
    }

    // Canvas Pie Chart
    const pieCanvas = document.getElementById('pie-chart');
    if (pieCanvas && results.length > 0) {
        const ctx = pieCanvas.getContext('2d');
        const width = pieCanvas.width = pieCanvas.parentElement.clientWidth;
        const height = pieCanvas.height = 250;
        
        const passed = results.filter(r => r.status === 'PASS').length;
        const failed = results.filter(r => r.status === 'FAIL').length;
        const total = passed + failed;
        
        if (total === 0) return;
        
        const cx = width / 2;
        const cy = height / 2;
        const radius = Math.min(width, height) / 2 - 20;
        
        let startAngle = 0;
        
        // Draw Pass Slice
        const passAngle = (passed / total) * 2 * Math.PI;
        ctx.fillStyle = '#10b981';
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, radius, startAngle, startAngle + passAngle);
        ctx.fill();
        
        startAngle += passAngle;
        
        // Draw Fail Slice
        const failAngle = (failed / total) * 2 * Math.PI;
        ctx.fillStyle = '#ef4444';
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, radius, startAngle, startAngle + failAngle);
        ctx.fill();
        
        // Draw inner circle for donut effect
        ctx.fillStyle = '#1e293b';
        ctx.beginPath();
        ctx.arc(cx, cy, radius * 0.6, 0, 2 * Math.PI);
        ctx.fill();
    }
}
