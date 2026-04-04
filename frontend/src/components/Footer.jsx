import { Link } from 'react-router-dom'

export default function Footer() {
    return (
        <footer className="footer">
            <div className="footer-inner">
                <div className="footer-brand">
                    <h3>🌾 TarımToptan</h3>
                    <p>
                        Tarım ürünleri satışı yapan e-ticaret platformumuz; nohut, pirinç, mercimek, bulgur gibi
                        temel gıda ürünlerini hızlı, kolay ve güvenilir bir şekilde ulaştırır.
                    </p>
                </div>

                <div className="footer-section">
                    <h4>Sayfalar</h4>
                    <Link to="/">Ana Sayfa</Link>
                    <Link to="/products">Ürünler</Link>
                    <Link to="/producers">Üreticiler</Link>
                </div>

                <div className="footer-section">
                    <h4>Hesap</h4>
                    <Link to="/login">Giriş Yap</Link>
                    <Link to="/register">Kayıt Ol</Link>
                    <Link to="/profile">Profil</Link>
                </div>

                <div className="footer-section">
                    <h4>İletişim</h4>
                    <a href="mailto:destek@tarimtoptan.com">destek@tarimtoptan.com</a>
                    <a href="https://api.yazmuh.com" target="_blank" rel="noopener noreferrer">API Dökümantasyonu</a>
                </div>
            </div>

            <div className="footer-bottom">
                <p>© 2026 TarımToptan — USA Grubu (Süleyman · Umut · Aykhan). Tüm hakları saklıdır.</p>
            </div>
        </footer>
    )
}
