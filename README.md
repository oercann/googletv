# Ev Panosu — Google TV için kendi ana ekranımız

Uygulamalar ızgarası + hava durumu + takvim + kamera önizlemeleri + hızlı ayarlar.
Kotlin + Jetpack Compose for TV. Android 8.0 ve üzeri (Chromecast with Google TV, Google TV televizyonlar).

## 1. Ayarları düzenle
`app/src/main/java/com/dobrum/evpanosu/Ayarlar.kt`
- **Konum**: hava durumu için enlem/boylam (Open-Meteo, anahtar gerekmez)
- **TAKVIM_ICS_URL**: Google Takvim → Ayarlar → takviminiz → "iCal biçimindeki gizli adres"
- **KAMERALAR**: her kamera için JPEG anlık görüntü adresi + RTSP/HLS yayın adresi

## 2. APK'yı derle (iki yol)
**A) GitHub (bilgisayar gerekmez):** Projeyi bir GitHub deposuna yükleyin. `.github/workflows/apk.yml`
her yüklemede APK'yı otomatik derler → Actions sekmesi → son çalışma → **ev-panosu-apk** dosyasını indirin.

**B) Android Studio:** Klasörü açın → Build → Build APK.

## 3. TV'ye yükle
- Telefona/TV'ye "Send Files to TV" uygulamasını kurun, APK'yı gönderin ve TV'de açın, ya da
- `adb connect TV_IP` → `adb install app-debug.apk`
- TV'de bilinmeyen kaynaklardan yüklemeye izin vermek gerekir: Ayarlar → Sistem → Hakkında → "Android TV OS derlemesi"ne 7 kez basın (Geliştirici modu) → Uygulamalar → Güvenlik → bilinmeyen kaynaklar.

## 4. Ana Sayfa tuşunu Ev Panosu'na bağla
Google TV kendi ana ekranını bırakmıyor. Bunun yerine:
**Ayarlar → Sistem → Erişilebilirlik → Ev Panosu → Aç.**
Bundan sonra kumandada Ana Sayfa'ya basınca Ev Panosu açılır.
Kapatmak için aynı yerden kapatın ya da uygulamayı kaldırın.

## Kullanım
- **OK**: uygulamayı aç
- **OK'a uzun bas**: uygulamayı listenin başına al
- **Kamera kutusu**: tam ekran canlı yayın, Geri ile dönüş
- **Ses / Uyku / Wi-Fi / Ayarlar**: ilgili TV ayar sayfasını açar

## Bilinen sınırlar
- Tekrarlayan takvim etkinlikleri yalnızca ilk tarihinde görünür.
- Kamera görüntüsünde yalnızca Basic Auth var. Digest Auth isteyen kameralarda önizleme gelmeyebilir; tam ekran RTSP yayın yine de çalışır.
- Kumandadaki profil ve canlı TV tuşları Google TV'nin kendi ekranlarını açmaya devam eder.
- Bu ortamda derleme yapılamadı; ilk derlemede çıkan hata olursa çıktıyı paylaşın, düzeltilir.
