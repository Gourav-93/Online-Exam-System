document.addEventListener('DOMContentLoaded', async () => {
    requireAuth('STUDENT');
    
    const urlParams = new URLSearchParams(window.location.search);
    const resultId = urlParams.get('id');
    
    if (!resultId) {
        window.location.href = 'student.html';
        return;
    }

    try {
        const result = await api.get(`/student/result/${resultId}`);
        renderResult(result);
    } catch (error) {
        console.error('Failed to load result:', error);
        document.getElementById('result-content').innerHTML = `
            <div class="card" style="text-align: center; color: var(--danger);">
                <h3>Failed to load result</h3>
                <button class="btn btn-primary" style="margin-top: 1rem;" onclick="window.location.href='student.html'">Back to Dashboard</button>
            </div>
        `;
    }
});

function renderResult(result) {
    const isPass = result.status === 'PASS';
    const maxScore = result.totalQuestions;
    const percentage = maxScore > 0 ? ((result.score / maxScore) * 100).toFixed(0) : 0;
    
    const content = document.getElementById('result-content');
    
    // Background effect
    if (isPass) {
        const bg = document.createElement('div');
        bg.className = 'bg-glow';
        bg.style.background = 'var(--success)';
        bg.style.top = '50%';
        bg.style.left = '50%';
        bg.style.transform = 'translate(-50%, -50%)';
        document.body.appendChild(bg);
    }

    content.innerHTML = `
        <div class="card glass animate-fade-in" style="text-align: center; padding: 3rem 2rem; position: relative; z-index: 10;">
            <div style="
                width: 80px; 
                height: 80px; 
                background: ${isPass ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)'}; 
                color: ${isPass ? 'var(--success)' : 'var(--danger)'};
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 3rem;
                margin: 0 auto 1.5rem auto;
            ">
                ${isPass ? '🏆' : '💔'}
            </div>
            
            <h2 style="font-size: 2.5rem; margin-bottom: 0.5rem;">${isPass ? 'Congratulations!' : 'Keep Trying!'}</h2>
            <p class="text-muted" style="margin-bottom: 2rem;">You have completed <span style="color: white; font-weight: 500;">${result.exam.title}</span></p>

            <div style="background: rgba(0,0,0,0.2); border: 1px solid var(--border-color); border-radius: 12px; padding: 1.5rem; margin-bottom: 2rem;">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
                    <div style="background: var(--bg-card); padding: 1rem; border-radius: 8px;">
                        <p class="text-muted" style="font-size: 0.9rem;">Your Score</p>
                        <p style="font-size: 2.5rem; font-weight: bold; color: ${isPass ? 'var(--success)' : 'var(--danger)'}">${result.score}</p>
                    </div>
                    <div style="background: var(--bg-card); padding: 1rem; border-radius: 8px;">
                        <p class="text-muted" style="font-size: 0.9rem;">Total Possible</p>
                        <p style="font-size: 2.5rem; font-weight: bold;">${maxScore}</p>
                    </div>
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; padding-top: 1rem; border-top: 1px solid var(--border-color);">
                    <span class="text-muted">Percentage</span>
                    <span style="font-weight: 500;">${percentage}%</span>
                </div>
                <div style="display: flex; justify-content: space-between; align-items: center; padding-top: 0.5rem;">
                    <span class="text-muted">Status</span>
                    <span style="padding: 0.2rem 0.8rem; border-radius: 20px; font-size: 0.9rem; font-weight: 500; background: ${isPass ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)'}; color: ${isPass ? 'var(--success)' : 'var(--danger)'};">
                        ${result.status}
                    </span>
                </div>
            </div>

            <button class="btn btn-outline" style="width: 100%; padding: 1rem;" onclick="window.location.href='student.html'">
                Back to Dashboard
            </button>
        </div>
    `;
}
