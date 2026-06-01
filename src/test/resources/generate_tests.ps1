Get-ChildItem "rv32ui-p-*" | ForEach-Object {
    $dirName = $_.Name
    $lastPart = ($dirName -split "-")[-1]
    $camel = ($lastPart.Substring(0,1).ToUpper() + $lastPart.Substring(1).ToLower())

    Get-ChildItem $_ -File | ForEach-Object {
        $baseName = [System.IO.Path]::GetFileNameWithoutExtension($_.Name)
@"
@Test
void RV32UI$camel() {
    try {
        var cpu = instantiateCPU("$baseName");
        runUntilExit(cpu);
        assertEquals(CPUStatus.CPUState.HALT_TEST_PASS, cpu.getStatus().status);

    } catch (Exception e) {
        assertFalse(false);
    }
}
"@

    }
}