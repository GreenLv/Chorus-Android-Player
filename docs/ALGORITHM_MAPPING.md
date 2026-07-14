# Algorithm mapping

| Public enum | Native ID | Display name | Log slug | Legacy alias, adapter-only |
|---|---:|---|---|---|
| `CHORUS` | 0 | Chorus | `Chorus` | `MPABR` |
| `XLINK` | 1 | XLINK | `XLINK` | `XABR` |
| `MIN_RTT_RI` | 2 | MinRTT+RI | `MinRTTRI` | `RABR` |
| `MIN_RTT` | 3 | MinRTT | `MinRTT` | `VABR` |
| `SP` | 4 | SP | `SP` | `SABR` |

The IDs are compatibility values and are covered by unit tests. Public UI, configuration, classes, and new logs use paper-facing names. Legacy names may appear only in a private adapter or migration explanation and do not grant access to the corresponding native implementation.
