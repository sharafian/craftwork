import { randomBytes } from 'crypto'

export * from './ServerKey'
export * from './DiscordServer'
export * from './MinecraftPlayer'
export * from './PlayerKey'
export * from './DiscordMember'
export * from './DiscordVoiceChannel'

export function base64url (buf: Buffer): string {
  return buf.toString('base64')
    .replace(/\//g, '_')
    .replace(/\+/g, '-')
    .replace(/=/g, '')
}

const MASK_LOWER_20 = 0xfffff
export function sixDigitCode () {
  while (true) {
    const candidate = randomBytes(4).readUInt32BE() & MASK_LOWER_20
    if (candidate < 1e6) return String(candidate).padStart(6, '0')
  }
}
