package cat.atridas.antagonista;

import java.util.HashMap;

public final class HashedString implements Comparable<HashedString> {
  
  private static final HashMap<Long, String> strings = new HashMap<>();
  
  public final long value;
  
  public HashedString(final String str) {
    value = computeCRC(str);

    String prev = strings.get(value);
    if(prev != null && !prev.equals(str)) {
      throw new RuntimeException("There are 2 strings with the same hash value: \"" + prev + "\" and \"" + str);
    } else {
      strings.put(value, str);
  	}
  }
  
  @Override
  public String toString() {
    return strings.get(value);
  }

  @Override
  public boolean equals(Object o) {
    if(o == null) return false;
    if(o.getClass() != HashedString.class) return false;
    
    return ((HashedString) o).value == value;
  }
  
  @Override
  public int compareTo(HashedString o) {
    return Long.signum(value - o.value);
  }

  @Override
  public int hashCode() {
    return (int) ((value & 0xFFFFFFFF0000l) >> 16);
  }
  
  public static long computeCRC(final String val) {
    long crc = 0;
    long x = 0;
    int  y = 0;
  
    crc = crc ^ (-1);
    byte[] bytes = val.getBytes();
    for(int i = 0; i < bytes.length; ++i)
    {
      byte b = bytes[i];
      y = (int) (crc ^ b) & 0xFF;
      assert(y < 256);
      x = crc64Table[y];
      boolean msb = (crc >> 63) != 0;
      crc = crc >> 8;
                        //0xffffffffffffffffl
      if(msb) crc = crc | 0xFF00000000000000l;
      crc = crc  ^ x;
    }
  
    return crc ^ (-1);
  }
  
  private static final long crc64Table[] = {
    0x0000000000000000l, 0x42F0E1EBA9EA3693l,
    0x85E1C3D753D46D26l, 0xC711223CFA3E5BB5l,
    0x493366450E42ECDFl, 0x0BC387AEA7A8DA4Cl,
    0xCCD2A5925D9681F9l, 0x8E224479F47CB76Al,
    0x9266CC8A1C85D9BEl, 0xD0962D61B56FEF2Dl,
    0x17870F5D4F51B498l, 0x5577EEB6E6BB820Bl,
    0xDB55AACF12C73561l, 0x99A54B24BB2D03F2l,
    0x5EB4691841135847l, 0x1C4488F3E8F96ED4l,
    0x663D78FF90E185EFl, 0x24CD9914390BB37Cl,
    0xE3DCBB28C335E8C9l, 0xA12C5AC36ADFDE5Al,
    0x2F0E1EBA9EA36930l, 0x6DFEFF5137495FA3l,
    0xAAEFDD6DCD770416l, 0xE81F3C86649D3285l,
    0xF45BB4758C645C51l, 0xB6AB559E258E6AC2l,
    0x71BA77A2DFB03177l, 0x334A9649765A07E4l,
    0xBD68D2308226B08El, 0xFF9833DB2BCC861Dl,
    0x388911E7D1F2DDA8l, 0x7A79F00C7818EB3Bl,
    0xCC7AF1FF21C30BDEl, 0x8E8A101488293D4Dl,
    0x499B3228721766F8l, 0x0B6BD3C3DBFD506Bl,
    0x854997BA2F81E701l, 0xC7B97651866BD192l,
    0x00A8546D7C558A27l, 0x4258B586D5BFBCB4l,
    0x5E1C3D753D46D260l, 0x1CECDC9E94ACE4F3l,
    0xDBFDFEA26E92BF46l, 0x990D1F49C77889D5l,
    0x172F5B3033043EBFl, 0x55DFBADB9AEE082Cl,
    0x92CE98E760D05399l, 0xD03E790CC93A650Al,
    0xAA478900B1228E31l, 0xE8B768EB18C8B8A2l,
    0x2FA64AD7E2F6E317l, 0x6D56AB3C4B1CD584l,
    0xE374EF45BF6062EEl, 0xA1840EAE168A547Dl,
    0x66952C92ECB40FC8l, 0x2465CD79455E395Bl,
    0x3821458AADA7578Fl, 0x7AD1A461044D611Cl,
    0xBDC0865DFE733AA9l, 0xFF3067B657990C3Al,
    0x711223CFA3E5BB50l, 0x33E2C2240A0F8DC3l,
    0xF4F3E018F031D676l, 0xB60301F359DBE0E5l,
    0xDA050215EA6C212Fl, 0x98F5E3FE438617BCl,
    0x5FE4C1C2B9B84C09l, 0x1D14202910527A9Al,
    0x93366450E42ECDF0l, 0xD1C685BB4DC4FB63l,
    0x16D7A787B7FAA0D6l, 0x5427466C1E109645l,
    0x4863CE9FF6E9F891l, 0x0A932F745F03CE02l,
    0xCD820D48A53D95B7l, 0x8F72ECA30CD7A324l,
    0x0150A8DAF8AB144El, 0x43A04931514122DDl,
    0x84B16B0DAB7F7968l, 0xC6418AE602954FFBl,
    0xBC387AEA7A8DA4C0l, 0xFEC89B01D3679253l,
    0x39D9B93D2959C9E6l, 0x7B2958D680B3FF75l,
    0xF50B1CAF74CF481Fl, 0xB7FBFD44DD257E8Cl,
    0x70EADF78271B2539l, 0x321A3E938EF113AAl,
    0x2E5EB66066087D7El, 0x6CAE578BCFE24BEDl,
    0xABBF75B735DC1058l, 0xE94F945C9C3626CBl,
    0x676DD025684A91A1l, 0x259D31CEC1A0A732l,
    0xE28C13F23B9EFC87l, 0xA07CF2199274CA14l,
    0x167FF3EACBAF2AF1l, 0x548F120162451C62l,
    0x939E303D987B47D7l, 0xD16ED1D631917144l,
    0x5F4C95AFC5EDC62El, 0x1DBC74446C07F0BDl,
    0xDAAD56789639AB08l, 0x985DB7933FD39D9Bl,
    0x84193F60D72AF34Fl, 0xC6E9DE8B7EC0C5DCl,
    0x01F8FCB784FE9E69l, 0x43081D5C2D14A8FAl,
    0xCD2A5925D9681F90l, 0x8FDAB8CE70822903l,
    0x48CB9AF28ABC72B6l, 0x0A3B7B1923564425l,
    0x70428B155B4EAF1El, 0x32B26AFEF2A4998Dl,
    0xF5A348C2089AC238l, 0xB753A929A170F4ABl,
    0x3971ED50550C43C1l, 0x7B810CBBFCE67552l,
    0xBC902E8706D82EE7l, 0xFE60CF6CAF321874l,
    0xE224479F47CB76A0l, 0xA0D4A674EE214033l,
    0x67C58448141F1B86l, 0x253565A3BDF52D15l,
    0xAB1721DA49899A7Fl, 0xE9E7C031E063ACECl,
    0x2EF6E20D1A5DF759l, 0x6C0603E6B3B7C1CAl,
    0xF6FAE5C07D3274CDl, 0xB40A042BD4D8425El,
    0x731B26172EE619EBl, 0x31EBC7FC870C2F78l,
    0xBFC9838573709812l, 0xFD39626EDA9AAE81l,
    0x3A28405220A4F534l, 0x78D8A1B9894EC3A7l,
    0x649C294A61B7AD73l, 0x266CC8A1C85D9BE0l,
    0xE17DEA9D3263C055l, 0xA38D0B769B89F6C6l,
    0x2DAF4F0F6FF541ACl, 0x6F5FAEE4C61F773Fl,
    0xA84E8CD83C212C8Al, 0xEABE6D3395CB1A19l,
    0x90C79D3FEDD3F122l, 0xD2377CD44439C7B1l,
    0x15265EE8BE079C04l, 0x57D6BF0317EDAA97l,
    0xD9F4FB7AE3911DFDl, 0x9B041A914A7B2B6El,
    0x5C1538ADB04570DBl, 0x1EE5D94619AF4648l,
    0x02A151B5F156289Cl, 0x4051B05E58BC1E0Fl,
    0x87409262A28245BAl, 0xC5B073890B687329l,
    0x4B9237F0FF14C443l, 0x0962D61B56FEF2D0l,
    0xCE73F427ACC0A965l, 0x8C8315CC052A9FF6l,
    0x3A80143F5CF17F13l, 0x7870F5D4F51B4980l,
    0xBF61D7E80F251235l, 0xFD913603A6CF24A6l,
    0x73B3727A52B393CCl, 0x31439391FB59A55Fl,
    0xF652B1AD0167FEEAl, 0xB4A25046A88DC879l,
    0xA8E6D8B54074A6ADl, 0xEA16395EE99E903El,
    0x2D071B6213A0CB8Bl, 0x6FF7FA89BA4AFD18l,
    0xE1D5BEF04E364A72l, 0xA3255F1BE7DC7CE1l,
    0x64347D271DE22754l, 0x26C49CCCB40811C7l,
    0x5CBD6CC0CC10FAFCl, 0x1E4D8D2B65FACC6Fl,
    0xD95CAF179FC497DAl, 0x9BAC4EFC362EA149l,
    0x158E0A85C2521623l, 0x577EEB6E6BB820B0l,
    0x906FC95291867B05l, 0xD29F28B9386C4D96l,
    0xCEDBA04AD0952342l, 0x8C2B41A1797F15D1l,
    0x4B3A639D83414E64l, 0x09CA82762AAB78F7l,
    0x87E8C60FDED7CF9Dl, 0xC51827E4773DF90El,
    0x020905D88D03A2BBl, 0x40F9E43324E99428l,
    0x2CFFE7D5975E55E2l, 0x6E0F063E3EB46371l,
    0xA91E2402C48A38C4l, 0xEBEEC5E96D600E57l,
    0x65CC8190991CB93Dl, 0x273C607B30F68FAEl,
    0xE02D4247CAC8D41Bl, 0xA2DDA3AC6322E288l,
    0xBE992B5F8BDB8C5Cl, 0xFC69CAB42231BACFl,
    0x3B78E888D80FE17Al, 0x7988096371E5D7E9l,
    0xF7AA4D1A85996083l, 0xB55AACF12C735610l,
    0x724B8ECDD64D0DA5l, 0x30BB6F267FA73B36l,
    0x4AC29F2A07BFD00Dl, 0x08327EC1AE55E69El,
    0xCF235CFD546BBD2Bl, 0x8DD3BD16FD818BB8l,
    0x03F1F96F09FD3CD2l, 0x41011884A0170A41l,
    0x86103AB85A2951F4l, 0xC4E0DB53F3C36767l,
    0xD8A453A01B3A09B3l, 0x9A54B24BB2D03F20l,
    0x5D45907748EE6495l, 0x1FB5719CE1045206l,
    0x919735E51578E56Cl, 0xD367D40EBC92D3FFl,
    0x1476F63246AC884Al, 0x568617D9EF46BED9l,
    0xE085162AB69D5E3Cl, 0xA275F7C11F7768AFl,
    0x6564D5FDE549331Al, 0x279434164CA30589l,
    0xA9B6706FB8DFB2E3l, 0xEB46918411358470l,
    0x2C57B3B8EB0BDFC5l, 0x6EA7525342E1E956l,
    0x72E3DAA0AA188782l, 0x30133B4B03F2B111l,
    0xF7021977F9CCEAA4l, 0xB5F2F89C5026DC37l,
    0x3BD0BCE5A45A6B5Dl, 0x79205D0E0DB05DCEl,
    0xBE317F32F78E067Bl, 0xFCC19ED95E6430E8l,
    0x86B86ED5267CDBD3l, 0xC4488F3E8F96ED40l,
    0x0359AD0275A8B6F5l, 0x41A94CE9DC428066l,
    0xCF8B0890283E370Cl, 0x8D7BE97B81D4019Fl,
    0x4A6ACB477BEA5A2Al, 0x089A2AACD2006CB9l,
    0x14DEA25F3AF9026Dl, 0x562E43B4931334FEl,
    0x913F6188692D6F4Bl, 0xD3CF8063C0C759D8l,
    0x5DEDC41A34BBEEB2l, 0x1F1D25F19D51D821l,
    0xD80C07CD676F8394l, 0x9AFCE626CE85B507l
  };
}
