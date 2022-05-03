export namespace KOJStorage {
  const storage = window.localStorage
  export const xIdentity = 'x-identity'

  export function identity(value?: string | undefined | null): string | null | undefined {
    if (value !== undefined) {
      if (value) {
        storage.setItem(xIdentity, value)
        return value
      } else {
        storage.removeItem(xIdentity)
      }
    } else {
      return storage.getItem(xIdentity)
    }
  }
}
