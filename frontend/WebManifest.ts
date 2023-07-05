import type { ManifestOptions } from 'vite-plugin-pwa'

export const manifest: Partial<ManifestOptions> = {
  name: 'Lost Ark Logs',
  short_name: 'LAL',
  description: 'Lost Ark Encounter Logging',
  theme_color: '#212121',
  start_url: '/',
  icons: [
    {
      src: '/icons/android-chrome-192x192.png',
      sizes: '192x192',
      type: 'image/png'
    },
    {
      src: '/icons/android-chrome-512x512.png',
      sizes: '512x512',
      type: 'image/png'
    },
    {
      src: '/icons/apple-touch-icon.png',
      sizes: '180x180',
      type: 'image/png'
    },
    {
      src: '/icons/favicon-16x16.png',
      sizes: '16x16',
      type: 'image/png'
    },
    {
      src: '/icons/favicon-32x32.png',
      sizes: '32x32',
      type: 'image/png'
    },
    {
      src: '/icons/favicon.ico',
      sizes: '48x48',
      type: 'image/ico'
    },
    {
      src: '/icons/mokoko.png',
      sizes: '400x400',
      type: 'image/png'
    },
    {
      src: '/icons/mask-icon.png',
      sizes: '400x400',
      type: 'image/png',
      purpose: 'maskable'
    }
  ],
  display: 'standalone'
}