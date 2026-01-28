import { describe, it, expect } from 'vitest'
import { Amount } from 'shared';

describe('Amount integration', () => {
  it('formats amount correctly for display', () => {
    const amount = new Amount(12599n)
    expect(amount.toFixed(2)).toBe((amount.apiFormat()).toFixed(2))
  })

  it('converts to API format', () => {
    const amount = new Amount(12599n)
    expect(amount.apiFormat()).toBe(125.99)
  })
})
