import React, { useEffect, useState } from 'react'
import api from '../services/api'

export default function Jobs() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function load() {
      try {
        const res = await api.get('/jobs')
        setJobs(res)
      } catch (err) {
        setError(err.message || 'Failed to load jobs')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  async function applyToJob(jobId) {
    if (!confirm('Apply to this job? Make sure you have uploaded your resume.')) return
    try {
      const result = await api.post('/applications/apply', { jobId })
      alert(`Applied! Match score: ${Math.round(result.matchScore)}%`)
      window.location.href = `/match-results.html?id=${result.id}`
    } catch (err) {
      alert(err.message)
    }
  }

  if (loading) return <div style={{padding:20}}>Loading jobs...</div>
  if (error) return <div style={{color:'var(--danger)',padding:20}}>{error}</div>

  return (
    <div style={{padding:20}}>
      <h2>Available Jobs</h2>
      <div style={{display:'grid',gap:12}}>
        {jobs.length === 0 && <div>No jobs found</div>}
        {jobs.map(j => (
          <div key={j.id} className="job-card" style={{cursor:'pointer'}} onClick={() => window.location.href = `/job-detail.html?id=${j.id}`}>
            <div className="job-card-header">
              <div>
                <h3>{j.title}</h3>
                <div className="company">{j.company}</div>
                <div className="location">📍 {j.location || 'Remote'}</div>
              </div>
              <span className={`badge badge-${j.jobType === 'FULL_TIME' ? 'green' : 'blue'}`}>{j.jobType.replace('_', ' ')}</span>
            </div>
            <p style={{color:'var(--text-secondary)',fontSize:14,margin:'0.75rem 0'}}>{j.description}</p>
            <div style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}>
              <div style={{color:'var(--accent-green)',fontWeight:600}}>{j.salaryRange || ''}</div>
              <button className="btn btn-primary btn-sm" onClick={(e) => { e.stopPropagation(); applyToJob(j.id) }}>Apply Now</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
