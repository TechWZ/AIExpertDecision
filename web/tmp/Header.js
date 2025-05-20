import React from 'react';
import { AppBar, Toolbar, Typography, Box, useTheme, useMediaQuery, Divider } from '@mui/material';

/**
 * 页面顶部栏组件
 * @returns {JSX.Element} 渲染的顶部栏组件
 */
const Header = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <AppBar
      position="fixed"
      sx={{
        boxShadow: 'none',
        zIndex: theme.zIndex.drawer + 1,
        backgroundColor: '#3a5ba0'
      }}
    >
      <Toolbar sx={{ minHeight: { xs: 64, sm: 100 } }}>
        <Box
          sx={{ 
            width: isMobile ? 70 : 80, 
            height: isMobile ? 70 : 80,
            mr: 2, 
            backgroundColor: theme.palette.grey[200], 
            borderRadius: '50%', 
            padding: '4px', 
            boxSizing: 'border-box',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden',
            flexShrink: 0, 
          }} 
        >
          <Box
            component="img"
            src="/logo2.png"
            alt="AigenMed Logo"
            sx={{
              height: isMobile ? 40 : 50, 
              objectFit: 'contain',
              maxWidth: '100%', 
              maxHeight: '100%', 
            }}
          />
        </Box>

        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          flexGrow: 1, 
          justifyContent: 'space-between' 
        }}>
          <Typography
            variant={isMobile ? "subtitle1" : "h5"}
            component="div"
            sx={{
              fontWeight: 'bold',
              color: 'white',
              whiteSpace: 'nowrap',
              letterSpacing: 1,
              textAlign: 'left', 
              minWidth: 0, 
              flexShrink: 1, 
            }}
          >
            国家健康医疗大数据研究院
          </Typography>

          <Divider orientation="vertical" flexItem sx={{ bgcolor: 'rgba(255, 255, 255, 0.5)', mx: 1.5, height: '24px', alignSelf: 'center' }} />

          <Typography
            variant={isMobile ? "body1" : "h4"} 
            component="div"
            sx={{
              fontWeight: 'bold',
              color: 'white',
              whiteSpace: 'nowrap',
              textAlign: 'center', 
              minWidth: 0, 
              flexShrink: 0.5, 
            }}
          >
            医学研究设计与分析AI智能体平台
          </Typography>

          <Divider orientation="vertical" flexItem sx={{ bgcolor: 'rgba(255, 255, 255, 0.5)', mx: 1.5, height: '24px', alignSelf: 'center' }} />
            
          <Typography
            variant={isMobile ? "subtitle1" : "h6"} 
            component="div"
            sx={{
              display: { xs: 'none', md: 'block' }, 
              fontStyle: 'normal',
              color: 'rgba(255, 255, 255, 0.9)',
              whiteSpace: 'nowrap',
              textAlign: 'right',
              minWidth: 0,
              flexShrink: 1,
            }}
          >
            AI Agent Platform for Medical Research Design and Analysis
          </Typography>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header; 