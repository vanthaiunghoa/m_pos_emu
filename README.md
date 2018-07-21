# POS_EMU_MODULES

## Description

pos_emu is a POS (Point Of Sale) Emulator developed in Java, this part contains the modules themselves

## Architecture

### modules

- m_common     : shared library with helpful functions and high level classes for underlying modules
- m_ecr        : Electronic Cash Register module which embbeds several protocol (a protocol is a derivation of m_ecr module)
- m_icc        : Smart-card management module. Can work with a PC/SC reader or without (virtual reader)

### Class view

- m_module
    - m_ecr
        - m_ecr_p9e
    - m_icc
        - m_icc_pcsc
        - m_icc_virtual
- C_logger
    - c_logger_stdout
- C_param
