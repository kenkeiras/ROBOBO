# Export the batery status of the phone

int32 STATUS_OK = 0
int32 STATUS_WARNING = 1
int32 STATUS_CRITICAL = 2

Header  header
float64 level        # % of batery charge [0..1]
int32   status       # Status of batery
string  description  # String [OK|WARNING|CRITICAL]