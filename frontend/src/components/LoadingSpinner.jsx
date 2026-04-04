export default function LoadingSpinner({ text = 'Yükleniyor...' }) {
    return (
        <div className="loading-container">
            <div className="spinner" />
            <p className="loading-text">{text}</p>
        </div>
    )
}
